package com.addnotes.fragment

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.addnotes.adapter.ViewPagerAdapter
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.add_notes_revamped_ui.databinding.RefactorHomeFragmentBinding
import com.addnotes.dialogs.DialogSelectionListener
import com.addnotes.dialogs.NotesTypeDialog
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<RefactorHomeFragmentBinding>(), View.OnClickListener,
    DialogSelectionListener {

    @Inject
    lateinit var customViewModelFactory: CustomViewModelFactory

    private lateinit var homeViewModel: HomeViewModel

    private var isFabOpen = false
    private lateinit var tts: TextToSpeech

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation

    private lateinit var fabAction: FloatingActionButton
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabDelete: FloatingActionButton
    private lateinit var fabShare: FloatingActionButton
    private lateinit var imageBackground: ImageView
    private lateinit var rlBackgroundNote: RelativeLayout

    private lateinit var listFragments: MutableList<Class<*>>
    private lateinit var showThemeDialog: ShowThemeDialog
    private lateinit var showNotesTypeDialog: NotesTypeDialog

    private lateinit var viewPagerAdapter: ViewPagerAdapter

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

        fabAction = binding!!.floatingActionsButtonsLayout.fabAction
        fabAdd = binding!!.floatingActionsButtonsLayout.fabAdd
        fabDelete = binding!!.floatingActionsButtonsLayout.fabDelete
        fabShare = binding!!.floatingActionsButtonsLayout.fabShare
        imageBackground = binding!!.contentMain.imageBackground
        rlBackgroundNote = binding!!.contentMain.rlBackgroundNote

        fabAction.setOnClickListener(this@HomeFragment)
        fabAdd.setOnClickListener(this@HomeFragment)
        fabDelete.setOnClickListener(this@HomeFragment)
        fabShare.setOnClickListener(this@HomeFragment)

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
                HomeViewModel.MYTHEMECOLOR,
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
                true, fabAction, fabAdd, fabDelete, fabShare, imageBackground, rlBackgroundNote
            )
        } else {
            Glide.with(requireContext())
                .load(R.drawable.super_man_logo)
                .fitCenter()
                .into(imageBackground)
            rlBackgroundNote.background =
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
        MobileAds.initialize(requireContext(), { })
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
        binding!!.floatingActionsButtonsLayout.welcomeViewPager.welcomeViewPagerLayout.visibility =
            View.VISIBLE
        fabAction.isClickable = false
        fabAdd.isClickable = false
        fabDelete.isClickable = false
        fabShare.isClickable = false

        listFragments = mutableListOf<Class<*>>()

        listFragments.add(WelcomeScreenOne::class.java)
        listFragments.add(WelcomeScreenTwo::class.java)
        listFragments.add(WelcomeScreenThree::class.java)

        viewPagerAdapter = ViewPagerAdapter(activity?.supportFragmentManager!!, requireContext())

        viewPagerAdapter.setFragments(listFragments)
        binding!!.floatingActionsButtonsLayout.welcomeViewPager.viewpager.adapter = viewPagerAdapter

        viewPagerAdapter.drawPageSelectionIndicators(
            binding!!.floatingActionsButtonsLayout.welcomeViewPager.viewPagerCountDots,
            listFragments, 0
        )
        binding!!.floatingActionsButtonsLayout.welcomeViewPager.viewpager.addOnPageChangeListener(
            object : OnPageChangeListener {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    viewPagerAdapter.drawPageSelectionIndicators(
                        binding!!.floatingActionsButtonsLayout
                            .welcomeViewPager.viewPagerCountDots,
                        listFragments, position
                    )
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
    }

    private fun animateFAB() {
        if (isFabOpen) {
            fabAction.startAnimation(rotateBackward)
            fabAdd.startAnimation(fabClose)
            fabDelete.startAnimation(fabClose)
            fabShare.startAnimation(fabClose)
            fabAdd.isClickable = false
            fabDelete.isClickable = false
            fabShare.isClickable = false
            isFabOpen = false
        } else {
            fabAction.startAnimation(rotateForward)
            fabAdd.startAnimation(fabOpen)
            fabDelete.startAnimation(fabOpen)
            fabShare.startAnimation(fabOpen)
            fabAdd.isClickable = true
            fabDelete.isClickable = true
            fabShare.isClickable = true
            isFabOpen = true
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
        showNotesTypeDialog()
    }

    private fun deleteNotes() {
        makeDeleteOptionVisible(View.VISIBLE)
    }

    private fun shareNotes() {
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

    private fun showNotesTypeDialog() {
        showNotesTypeDialog = NotesTypeDialog(requireContext(), this)
    }

    fun onSkipClicked() {
        fabAction.isClickable = true
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
        const val TAG = "Home_Fragment"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onDialogInputSelected(id: Int) {
//        addNotesPresenter.userSelection(v.getId())
//        val editor: SharedPreferences.Editor = getSharedPreferences(
//            addnote.vnps.addnotes.addnotes.view.AddNotesView.MyPREFERENCES,
//            Context.MODE_PRIVATE
//        ).edit()
        var themeColor: String = StringUtilities.EMPTY_STRING
        var isThemeSelected = true

        val window: Window? = activity?.window

        when (id) {
            R.id.rlRedYellow -> {
                themeColor = getString(R.string.redYellow)
            }
            R.id.rlPurpleGreen -> {
                themeColor = getString(R.string.purpleGreen)
            }
            R.id.rlOrangRed -> {
                themeColor = getString(R.string.orangeRed)
            }
            R.id.rlPinkYellow -> {
                themeColor = getString(R.string.pinkYellow)
            }
            R.id.rlGreenBrown -> {
                themeColor = getString(R.string.greenBrown)
            }
            R.id.rlblue -> {
                themeColor = getString(R.string.blue)
            }
            R.id.rlPink_helloKitty -> {
                themeColor = getString(R.string.pink_hello_kitty)
            }
            R.id.rlOrange -> {
                themeColor = getString(R.string.orange)
            }
            R.id.rlDeepPurple -> {
                themeColor = getString(R.string.deepPurple)
            }
            R.id.rlBlackRed -> {
                themeColor = getString(R.string.blackRed)
            }
            R.id.rlNeonBlue -> {
                themeColor = getString(R.string.neonBlue)
            }
            R.id.rlWhiteJoker -> {
                themeColor = getString(R.string.whiteJoker)

            }
            R.id.rlShoppingSelected -> {
//            val type = Bundle()
//            val intent = Intent(activity?.applicationContext, ShoppingNote::class.java)
//           intent.putExtra(
//               NOTESACTIVITY_TYPE_KEY,
//              NOTESACTIVITY_TYPE_ADD
//           )
//            intent.putExtras(type)
                //startActivity(intent)
                //  dismiss()
            }
            R.id.rlNoteSelected -> {
                val type = Bundle()
//            val intent = Intent(getApplicationContext(), EditNotesView::class.java)
//            intent.putExtra(
//                addnote.vnps.addnotes.addnotes.view.AddNotesView.NOTESACTIVITY_TYPE_KEY,
//                addnote.vnps.addnotes.addnotes.view.AddNotesView.NOTESACTIVITY_TYPE_ADD
//            )
//            intent.putExtras(type)
//            startActivity(intent)
                //     dismiss()
            }
            else -> {}
        }
        if (isThemeSelected) {
            homeViewModel.updateThemeInPreferences(themeColor)
            ThemesEngine.updateThemeColors(
                requireContext(),
                themeColor,
                binding!!.toolbar.toolbar,
                null,
                window,
                true,
                fabAction, fabAdd, fabDelete, fabShare, imageBackground, rlBackgroundNote
            )
            showThemeDialog.dismiss()
        }
    }
}