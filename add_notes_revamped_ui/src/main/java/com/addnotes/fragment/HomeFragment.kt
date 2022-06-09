package com.addnotes.fragment

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.transition.AutoTransition
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.addnotes.adapter.ViewPagerAdapter
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.add_notes_revamped_ui.databinding.RefactorHomeFragmentBinding
import com.addnotes.dialogs.ShowThemeDialog
import com.addnotes.fragment.welcome.WelcomeScreenOne
import com.addnotes.fragment.welcome.WelcomeScreenThree
import com.addnotes.fragment.welcome.WelcomeScreenTwo
import com.addnotes.injection.CustomViewModelFactory
import com.addnotes.utils.StringUtilities
import com.addnotes.utils.ThemesEngine
import com.addnotes.viewModel.HomeViewModel
import com.addnotes.viewModel.StateData
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<RefactorHomeFragmentBinding>(), View.OnClickListener,
    ShowThemeDialog.ThemeDialogSelectionListener {

    @Inject
    lateinit var customViewModelFactory: CustomViewModelFactory

    private lateinit var homeViewModel: HomeViewModel

    private var isFabOpen = false
    private lateinit var tts: TextToSpeech

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation

    lateinit var listFragments: MutableList<Class<*>>
    lateinit var showThemeDialog: ShowThemeDialog

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        binding = RefactorHomeFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel = ViewModelProviders
            .of(this, customViewModelFactory).get(HomeViewModel::class.java)

        setToolBar()
        //Animations
        fabOpen = AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.fab_close)
        rotateForward =
            AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.rotate_forward)
        rotateBackward =
            AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.rotate_backward)

        binding?.apply {
            floatingActionsButtonsLayout.fabAction.setOnClickListener(this@HomeFragment)
            floatingActionsButtonsLayout.fabAdd.setOnClickListener(this@HomeFragment)
            floatingActionsButtonsLayout.fabDelete.setOnClickListener(this@HomeFragment)
            floatingActionsButtonsLayout.fabShare.setOnClickListener(this@HomeFragment)
        }

        tts = TextToSpeech(
            activity?.applicationContext
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.US
                tts.setSpeechRate(1f)
            }
        }

        setInitialTheme()
        setGridViewForNotes(false)
        displayADD()

        homeViewModel.getFirstLaunchDetails()
            .observe(viewLifecycleOwner, processFirstLaunchDetails())
        homeViewModel.checkFirstLaunch()
    }

    private fun setToolBar() {
        try {
            (activity as AppCompatActivity).setSupportActionBar(binding!!.toolbar.toolbar)
        } catch (e: Exception) {
            Log.d("ToolBar Error", "ClassCastException")
        }
    }

    private fun setInitialTheme() {
        val themeColor: String? =
            homeViewModel.getValueFromSharedPreference(
                MYTHEMECOLOR,
                StringUtilities.EMPTY_STRING
            )

        val window: Window? = activity?.window

        if (!TextUtils.isEmpty(themeColor) && window != null) {
            ThemesEngine.updateThemeColors(
                requireContext(),
                themeColor,
                binding!!.toolbar.toolbar,
                null,
                window,
                true,
                binding!!.floatingActionsButtonsLayout.fabAction,
                binding!!.floatingActionsButtonsLayout.fabAdd,
                binding!!.floatingActionsButtonsLayout.fabDelete,
                binding!!.floatingActionsButtonsLayout.fabShare,
                binding!!.contentMain.imageBackground,
                binding!!.contentMain.rlBackgroundNote
            )
        } else {
            Glide.with(requireContext())
                .load(R.drawable.super_man_logo)
                .centerCrop()
                .into(binding!!.contentMain.imageBackground)
            binding!!.contentMain.rlBackgroundNote.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.app_bg3_c)
        }
    }

    /**
     * Setting the number of notes in a row according to the screen size.
     *
     * @param isRenderViewCalled
     */
    private fun setGridViewForNotes(isRenderViewCalled: Boolean) {
        val scaleFactor = resources.displayMetrics.density * 120
        val number: Int = activity?.windowManager?.defaultDisplay?.width!!

        val columns = (number.toFloat() / scaleFactor).toInt()
        if (binding!!.contentMain.notesContainer.childCount > 0) {
            binding!!.contentMain.notesContainer.removeAllViews()
        }
        binding!!.contentMain.notesContainer.columnCount = columns
        if (isRenderViewCalled) {
            //  addNotesPresenter.renderView(0)
        }
    }

    /*
    * Display Add
    *
    */
    private fun displayADD() {
        val adView = binding!!.floatingActionsButtonsLayout.adView
        // Request for Ads
        val adRequest = AdRequest.Builder().build()
        MobileAds.initialize(requireContext(),
            OnInitializationCompleteListener { })
        adView.loadAd(adRequest)
    }

    private fun processFirstLaunchDetails(): Observer<StateData<Boolean>> =
        Observer {
            if (it != null) {
                when (it.getStatus()) {
                    StateData.DataStatus.SUCCESS -> {
                        if (it.getData() == true) {
                            setFirstTimeUserExperience()
                        } else {
                            binding?.apply {
                                floatingActionsButtonsLayout.welcomeViewPager.welcomeViewPagerLayout.visibility =
                                    View.GONE
                                floatingActionsButtonsLayout.fabAction.isClickable = true
                            }
                        }
                    }
                    else -> {
                        //
                    }
                }
            }
        }

    private fun setFirstTimeUserExperience() {

        animateFAB()
        binding?.apply {
            floatingActionsButtonsLayout.welcomeViewPager.welcomeViewPagerLayout.visibility =
                View.VISIBLE
            floatingActionsButtonsLayout.fabAction.isClickable = false
            floatingActionsButtonsLayout.fabAdd.isClickable = false
            floatingActionsButtonsLayout.fabDelete.isClickable = false
            floatingActionsButtonsLayout.fabShare.isClickable = false
        }
        listFragments = mutableListOf<Class<*>>()

        listFragments.add(WelcomeScreenOne::class.java)
        listFragments.add(WelcomeScreenTwo::class.java)
        listFragments.add(WelcomeScreenThree::class.java)

        val viewPagerAdapter = ViewPagerAdapter(activity?.supportFragmentManager!!)

        viewPagerAdapter.setFragments(listFragments)
        binding!!.floatingActionsButtonsLayout.welcomeViewPager.viewpager.adapter = viewPagerAdapter

        drawPageSelectionIndicators(0)
        binding!!.floatingActionsButtonsLayout.welcomeViewPager.viewpager.addOnPageChangeListener(
            object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    drawPageSelectionIndicators(position)
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
    }

    fun animateFAB() {
        if (isFabOpen) {
            binding?.apply {
                floatingActionsButtonsLayout.fabAction.startAnimation(rotateBackward)
                floatingActionsButtonsLayout.fabAdd.startAnimation(fabClose)
                floatingActionsButtonsLayout.fabDelete.startAnimation(fabClose)
                floatingActionsButtonsLayout.fabShare.startAnimation(fabClose)
                floatingActionsButtonsLayout.fabAdd.isClickable = false
                floatingActionsButtonsLayout.fabDelete.isClickable = false
                floatingActionsButtonsLayout.fabShare.isClickable = false
            }
            isFabOpen = false
        } else {
            binding?.apply {
                floatingActionsButtonsLayout.fabAction.startAnimation(rotateForward)
                floatingActionsButtonsLayout.fabAdd.startAnimation(fabOpen)
                floatingActionsButtonsLayout.fabDelete.startAnimation(fabOpen)
                floatingActionsButtonsLayout.fabShare.startAnimation(fabOpen)
                floatingActionsButtonsLayout.fabAdd.isClickable = true
                floatingActionsButtonsLayout.fabDelete.isClickable = true
                floatingActionsButtonsLayout.fabShare.isClickable = true
            }
            isFabOpen = true
        }
    }


    private fun drawPageSelectionIndicators(mPosition: Int) {
        if (binding!!.floatingActionsButtonsLayout.welcomeViewPager.viewPagerCountDots != null) {
            binding!!.floatingActionsButtonsLayout.welcomeViewPager.viewPagerCountDots.removeAllViews()
        }
        val dots = arrayOfNulls<ImageView>(listFragments.size)
        for (i in listFragments.indices) {
            dots[i] = ImageView(context)
            if (i == mPosition) {
                dots[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.item_selected
                    )
                )
            } else {
                dots[i]?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.item_unselected
                    )
                )
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(15, 0, 15, 0)
            binding!!.floatingActionsButtonsLayout.welcomeViewPager.viewPagerCountDots.addView(
                dots.get(
                    i
                ), params
            )
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fab_action -> viewOptions()
            R.id.fab_add -> addNotes()
            R.id.fab_delete -> deleteNotes()
            R.id.fab_share -> shareNotes()
            else -> {}
        }
    }

    private fun viewOptions() {
        animateFAB()
        makeDeleteOptionVisible(View.GONE)
        makeShareOptionVisible(View.GONE)
    }

    /**
     * Opens the EditNotesView for the user to add a new note.
     */
    fun addNotes() {
        makeDeleteOptionVisible(View.GONE)
        makeShareOptionVisible(View.GONE)
        setType()
    }

    fun deleteNotes() {
        makeDeleteOptionVisible(View.VISIBLE)
    }

    fun shareNotes() {
        makeShareOptionVisible(View.VISIBLE)
    }

    /**
     * Makes the delete option visible.
     *
     * @param visibility
     */
    private fun makeDeleteOptionVisible(visibility: Int) {
        for (child in 0 until binding!!.contentMain.notesContainer.childCount) {
            val view = binding!!.contentMain.notesContainer.getChildAt(child)
            val imgDeleteNote = view.findViewById<View>(R.id.imgDeleteNote) as ImageView
            imgDeleteNote.visibility = visibility
            imgDeleteNote.isEnabled = false
            if (visibility == View.VISIBLE) {
                imgDeleteNote.isEnabled = true
            }
        }
    }

    /**
     * Makes the share option visible.
     *
     * @param visibility
     */
    private fun makeShareOptionVisible(visibility: Int) {
        for (child in 0 until binding!!.contentMain.notesContainer.childCount) {
            val view = binding!!.contentMain.notesContainer.getChildAt(child)
            val imgShareNote = view.findViewById<View>(R.id.imgShareNote) as ImageView
            imgShareNote.visibility = visibility
            imgShareNote.isEnabled = false
            if (visibility == View.VISIBLE) {
                imgShareNote.isEnabled = true
            }
        }
    }

    private fun setType() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.exitTransition = AutoTransition()
        dialog.window!!.enterTransition = AutoTransition()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        activity?.setFinishOnTouchOutside(false)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.row_type_of_note)
        val rlShoppingSelected =
            dialog.findViewById<View>(R.id.rlShoppingSelected) as RelativeLayout
        val rlNoteSelected = dialog.findViewById<View>(R.id.rlNoteSelected) as RelativeLayout
        rlShoppingSelected.setOnClickListener {
//            val type = Bundle()
//            val intent = Intent(activity?.applicationContext, ShoppingNote::class.java)
//           intent.putExtra(
//               NOTESACTIVITY_TYPE_KEY,
//              NOTESACTIVITY_TYPE_ADD
//           )
//            intent.putExtras(type)
            //startActivity(intent)
            dialog.dismiss()
        }
        rlNoteSelected.setOnClickListener {
            val type = Bundle()
//            val intent = Intent(getApplicationContext(), EditNotesView::class.java)
//            intent.putExtra(
//                addnote.vnps.addnotes.addnotes.view.AddNotesView.NOTESACTIVITY_TYPE_KEY,
//                addnote.vnps.addnotes.addnotes.view.AddNotesView.NOTESACTIVITY_TYPE_ADD
//            )
//            intent.putExtras(type)
//            startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }

    fun onSkipClicked() {
        binding!!.floatingActionsButtonsLayout.fabAction.isClickable = true
        binding!!.floatingActionsButtonsLayout.welcomeViewPager.welcomeViewPagerLayout.visibility =
            View.GONE
        animateFAB()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_theme, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_theme -> {
                showThemeDialog()
                return true
            }
            R.id.action_dropBox -> {
                //showDropBox()
                return true
            }
            R.id.action_rate_us -> {
                //rateApp()
                return true
            }
            R.id.action_about_us -> {
                /// aboutUs()
                return true
            }
            else -> {
//
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showThemeDialog() {
        showThemeDialog = ShowThemeDialog(requireContext(), this)
    }

    companion object {

        const val NOTESACTIVITY_TYPE_KEY = "type_key"
        const val NOTESACTIVITY_TYPE_UPDATE = "type_update"
        const val NOTESACTIVITY_TYPE_ADD = "type_add"
        const val NOTESACTIVITY_TYPE_POSITION = "type_position"
        const val PREF_VERSION_CODE_KEY = "version_code"
        const val MYTHEMECOLOR = "myThemeColor"
        const val TAG = "Home_Fragment"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onThemeSelected(id: Int) {
        //addNotesPresenter.userSelection(v.getId())
//        val editor: SharedPreferences.Editor = getSharedPreferences(
//            addnote.vnps.addnotes.addnotes.view.AddNotesView.MyPREFERENCES,
//            Context.MODE_PRIVATE
//        ).edit()
//        var themeColor = ""
//        val window: Window = this.getWindow()
//        when (v.getId()) {
//            R.id.rlRedYellow -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.redYellow)
//                )
//                editor.commit()
//                themeColor = getString(R.string.redYellow)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            R.id.rlPurpleGreen -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.purpleGreen)
//                )
//                editor.commit()
//                themeColor = getString(R.string.purpleGreen)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            R.id.rlOrangRed -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.orangeRed)
//                )
//                editor.commit()
//                themeColor = getString(R.string.orangeRed)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            R.id.rlPinkYellow -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.pinkYellow)
//                )
//                editor.commit()
//                themeColor = getString(R.string.pinkYellow)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            R.id.rlGreenBrown -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.greenBrown)
//                )
//                editor.commit()
//                themeColor = getString(R.string.greenBrown)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            R.id.rlblue -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.blue)
//                )
//                editor.commit()
//                themeColor = getString(R.string.blue)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            R.id.rlPink_helloKitty -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.pink_hello_kitty)
//                )
//                editor.commit()
//                themeColor = getString(R.string.pink_hello_kitty)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            R.id.rlOrange -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.orange)
//                )
//                editor.commit()
//                themeColor = getString(R.string.orange)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            R.id.rlDeepPurple -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.deepPurple)
//                )
//                editor.commit()
//                themeColor = getString(R.string.deepPurple)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            R.id.rlBlackRed -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.blackRed)
//                )
//                editor.commit()
//                themeColor = getString(R.string.blackRed)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            R.id.rlNeonBlue -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.neonBlue)
//                )
//                editor.commit()
//                themeColor = getString(R.string.neonBlue)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            R.id.rlWhiteJoker -> {
//                editor.putString(
//                    addnote.vnps.addnotes.addnotes.view.AddNotesView.MYTHEMECOLOR,
//                    getString(R.string.whiteJoker)
//                )
//                editor.commit()
//                themeColor = getString(R.string.whiteJoker)
//                CommonUtilities.changeThemeColors(
//                    themeColor,
//                    binding!!.toolbar.toolbar,
//                    null,
//                    window,
//                    true,
//                    binding!!.floatingActionsButtonsLayout.fabAction,
//                    binding!!.floatingActionsButtonsLayout.fabAdd,
//                    binding!!.floatingActionsButtonsLayout.fabDelete,
//                    binding!!.floatingActionsButtonsLayout.fabShare,
//                    binding!!.contentMain.imageBackground,
//                    binding!!.contentMain.rlBackgroundNote
//                )
//                dialogTheme.dismiss()
//            }
//            else -> {}
//        }
    }
}