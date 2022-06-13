package com.addnotes.fragment.viewnotes

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.CompoundButton
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addnotes.adapter.AdapterClickListener
import com.addnotes.adapter.ShoppingListAdapter
import com.addnotes.adapter.ShoppingListData
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.add_notes_revamped_ui.databinding.ActivityShoppingBinding
import com.addnotes.dialogs.DialogSelectionListener
import com.addnotes.dialogs.ShoppingListItemDetailsDialog
import com.addnotes.fragment.home.HomeFragment
import com.addnotes.interfaces.FragmentBackPressed
import com.addnotes.utils.ShoppingCalculator
import com.addnotes.utils.StringUtilities
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateShoppingNotesFragment : BaseNotesFragment<ActivityShoppingBinding>(),
    FragmentBackPressed {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    lateinit var shoppingListAdapter: ShoppingListAdapter
    lateinit var shoppingListArray: ArrayList<ShoppingListData>
    lateinit var listView: RecyclerView
    lateinit var shoppingListItemDetailsDialog: ShoppingListItemDetailsDialog

    private var notesType = StringUtilities.EMPTY_STRING
    private var position: Int = 0

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        binding = ActivityShoppingBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(binding!!.toolbar.toolbar)
        setTheme(sharedPreferences, binding!!.toolbar.toolbar)

        shoppingListArray = arrayListOf<ShoppingListData>()

        listView = binding!!.shoppingListView.listView
        shoppingListAdapter = ShoppingListAdapter(requireContext(), shoppingListArray,
            object : AdapterClickListener {
                override fun onItemClick(position: Int, v: View?) {
                    if (v?.id == R.id.imgDeleteNote) {
                        shoppingListArray.removeAt(position)

                        setTotalItemSum()

                        shoppingListAdapter.notifyDataSetChanged()
                    } else {
                        shoppingListItemDetailsDialog = ShoppingListItemDetailsDialog(
                            requireContext(), object : DialogSelectionListener {
                                override fun onDialogInputSelected(id: Int) {
                                    shoppingListAdapter.notifyDataSetChanged()
                                    setTotalItemSum()

                                    shoppingListItemDetailsDialog.dismiss()

                                    activity?.window!!
                                        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                                }
                            },
                            shoppingListArray,
                            position
                        )
                    }
                }
            },
            object : ShoppingListAdapter.MyCheckedChangeListener {
                override fun onCheckedChanged(
                    position: Int,
                    buttonView: CompoundButton?,
                    isChecked: Boolean
                ) {
                    if (buttonView!!.isShown) {
                        if (!isChecked) {
                            buttonView.isChecked = true
                            shoppingListArray[position].doneOrNot = "yes"
                        } else {
                            buttonView.isChecked = false
                            shoppingListArray[position].doneOrNot = "no"
                        }
                        shoppingListAdapter.notifyDataSetChanged()
                    }
                }
            }
        )
        listView.layoutManager = LinearLayoutManager(requireContext())
        listView.setHasFixedSize(true)
        listView.itemAnimator = DefaultItemAnimator()
        listView.adapter = shoppingListAdapter

        checkIfUpdateShoppingNote()
        binding!!.fabSave.setOnClickListener(View.OnClickListener {
            if (!binding!!.shoppingListView.etNoteText.text.toString()
                    .equals(StringUtilities.EMPTY_STRING, ignoreCase = true)
            ) {
                val shoppingListData = ShoppingListData(
                    binding!!.shoppingListView.etNoteText.text.toString(),
                    StringUtilities.EMPTY_STRING,
                    StringUtilities.EMPTY_STRING,
                    "no"
                )
                shoppingListArray.add(shoppingListData)
                shoppingListAdapter.notifyDataSetChanged()

                setTotalItemSum()

                binding!!.shoppingListView.etNoteText.setText(StringUtilities.EMPTY_STRING)
            } else {
                val snackbar = Snackbar
                    .make(
                        binding!!.shoppingNoteCoordinatorLayout,
                        "Oops!! Please Enter the item to buy..",
                        Snackbar.LENGTH_SHORT
                    )
                snackbar.setActionTextColor(Color.RED)
                snackbar.show()
            }
        })
    }

    private fun checkIfUpdateShoppingNote() {

        val bundle: Bundle? = activity?.intent?.extras

        (bundle?.get(HomeFragment.NOTES_TYPE_KEY)?.let {
            notesType = it.toString()
            // TODO: Complete DB and return.
//            if (notesType.equals(HomeFragment.NOTES_TYPE_UPDATE, ignoreCase = true)) {
//
//                position = bundle.get(HomeFragment.NOTES_TYPE_POSITION).toString().toInt()
//                //val noteDetails: NoteDetails = database.getNote(position)
//                //binding?.shoppingListView?.titleShopping?.text = noteDetails.getShoppingTitle()
//
//                val json: JSONObject
//                try {
//                    json = JSONObject(noteDetails.getShoppingString())
//                    if (json.optJSONArray(getString(R.string.json_Shopping_Array)) != null) {
//                        val jArray = json.optJSONArray(getString(R.string.json_Shopping_Array))
//                        for (i in 0 until jArray.length()) {
//                            val json_obj = jArray.getJSONObject(i) //get the 3rd item
//                            val shoppingPojo = ShoppingListData(
//                                json_obj.getString("itemToBuy"),
//                                json_obj.getString("value"),
//                                json_obj.getString("count"),
//                                json_obj.getString("doneOrNot")
//                            )
//                            shoppingListArray.add(shoppingPojo)
//                        }
//                    }
//                    shoppingListAdapter.notifyDataSetChanged()
//                    setTotalItemSum()
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//                if (!noteDetails.getAlertTime()
//                        .equalsIgnoreCase(getString(R.string.alertDefaultValue))
//                ) {
//                    binding?.shoppingListView?.alertText?.visibility = View.VISIBLE
//                    //binding?.shoppingListView?.alertText?.text = noteDetails.getAlertTime()
//                }
//            }
        })
    }

    private fun setTotalItemSum() {
        val sum = ShoppingCalculator.doSumOfShoppingItems(shoppingListArray)
        if (sum != 0) {
            binding!!.shoppingListView.totalSumItem.setText(
                getString(
                    R.string.shopping_total_amount,
                    sum
                )
            )
            binding!!.shoppingListView.totalSumItem.visibility = View.VISIBLE
        } else {
            binding!!.shoppingListView.totalSumItem.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_shopping, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onBackPressed(): Boolean {
        return if (shoppingListArray.size > 0) {
            // TODO: saveShoppingDetails()
            Toast.makeText(requireContext(), "Please Save", Toast.LENGTH_LONG).show()
            true
        } else {
            false
        }
    }

    companion object {
        const val TAG: String = "ViewShoppingNotesFragment"

        fun newInstance(): CreateShoppingNotesFragment {
            return CreateShoppingNotesFragment()
        }
    }
}