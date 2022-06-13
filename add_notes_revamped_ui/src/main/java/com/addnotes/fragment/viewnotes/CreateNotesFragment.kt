package com.addnotes.fragment.viewnotes

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.addnotes.add_notes_revamped_ui.R
import com.addnotes.add_notes_revamped_ui.databinding.ActivityAddnoteBinding
import com.addnotes.dialogs.DialogSelectionListener
import com.addnotes.dialogs.FontSelectionListener
import com.addnotes.dialogs.FontsListDialog
import com.addnotes.dialogs.NotesColorOptionsDialog
import com.addnotes.fragment.home.HomeFragment
import com.addnotes.injection.CustomViewModelFactory
import com.addnotes.interfaces.FragmentBackPressed
import com.addnotes.utils.NetworkUtilities
import com.addnotes.utils.StringUtilities
import com.addnotes.viewModel.CreateNotesViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import yuku.ambilwarna.AmbilWarnaDialog
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CreateNotesFragment : BaseNotesFragment<ActivityAddnoteBinding>(), View.OnClickListener,
    FragmentBackPressed {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var customViewModelFactory: CustomViewModelFactory

    private lateinit var createNotesViewModel: CreateNotesViewModel
    private lateinit var colorOptionsDialog: NotesColorOptionsDialog

    private lateinit var slideInLeft: Animation
    private lateinit var fabClose: Animation
    private lateinit var slideInRight: Animation
    private lateinit var slideOutRight: Animation
    private lateinit var slideOutLeft: Animation
    private lateinit var tts: TextToSpeech

    var colorDefault = "#EDEDED"
    var colorTextDefault = "#3C5899"
    private var fontsSelected = "default"

    var color = -0x100
    private var isUpdateWidget = false
    private var isNotePresent = false
    private var isWidgetView = false
    private var position: Int = 0
    private var getWidgetIdNotes = 0
    var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID


    var titleLocked = StringUtilities.EMPTY_STRING
    var lockedPassword = StringUtilities.EMPTY_STRING
    var lockedOrnot = 0

    var notesType: String = StringUtilities.EMPTY_STRING

    override fun onCreateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) {
        binding = ActivityAddnoteBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolBar(binding!!.toolbar.toolbar)
        setTheme(sharedPreferences, binding!!.toolbar.toolbar)

        createNotesViewModel = ViewModelProviders
            .of(this, customViewModelFactory).get(CreateNotesViewModel::class.java)

        slideInLeft =
            AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.slide_in_left)
        slideInRight =
            AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.slide_in_right)
        slideOutRight =
            AnimationUtils.loadAnimation(
                activity?.applicationContext,
                android.R.anim.slide_out_right
            )
        slideOutLeft =
            AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.slide_out_left)
        // TODO: Analytics
        //TODO: DATABASE

        //database = DatabaseHandler(this)
        binding!!.createNotesContent.rlNote.setBackgroundColor(Color.parseColor(colorDefault))

        binding!!.fabSave.setOnClickListener(this)
        binding!!.createNotesContent.btnTTSPause.setOnClickListener(this)
        binding!!.createNotesContent.btnSpeak.setOnClickListener(this)
        binding!!.createNotesContent.btnTTSstart.setOnClickListener(this)
        binding!!.createNotesContent.rlNote.setOnClickListener(this)

        val bundle: Bundle? = activity?.intent?.extras

        if (!createNotesViewModel.isWidgetFlowSelected(activity?.intent)) {
            isWidgetView = false

            getNonWidgetUI(bundle)
        } else {
            isWidgetView = true
            activity?.invalidateOptionsMenu()

            getWidgetUI(bundle)
        }

        tts = TextToSpeech(activity?.applicationContext) { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.US
                tts.setSpeechRate(1f)
            }
        }
        ttsListeners()
        displayADD()
    }

    private fun getWidgetUI(bundle: Bundle?) {
        if (bundle != null) {
            mAppWidgetId = bundle.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }
        //TODO:
        //  val noteDetailsList: List<NoteDetails> = database.getAllNotes()
        //already present widget.
        //already present widget.
        if (activity?.intent?.getStringExtra("widgetNoteDetails") != null) {
//            for (i in noteDetailsList.indices) {
//                if (noteDetailsList[i].getNote()
//                        .equalsIgnoreCase(activity?.intent?.getStringExtra("widgetNoteDetails"))
//                    && !noteDetailsList[i].getTypeOfNote().equalsIgnoreCase("Shopping")
//                ) {
            isUpdateWidget = true
            isNotePresent = true
//            position = i + 1
//                    val noteDetails: NoteDetails = database.getNote(position)
//                    if (database.getNote(position).getLockedOrNot() === 1) {
//                        lockedPassword = database.getNote(position).getLockPassword()
//                        lockedOrnot = database.getNote(position).getLockedOrNot()
//                        titleLocked = database.getNote(position).getLockTitle()
//                    }
//                    setDetailsToView(noteDetails)
//                    break
//                }
//            }
            if (!isNotePresent) {
                isUpdateWidget = false
                binding!!.createNotesContent.etNoteText.setText(activity?.intent?.getStringExtra("widgetNoteDetails"))
            }
        } else {
            //1st time adding widget.
            val noteText: String? =
                createNotesViewModel.getValueFromSharedPreference(
                    CreateNotesViewModel.PREF_WIDGET_PREFIX_KEY + mAppWidgetId,
                    StringUtilities.EMPTY_STRING
                )

//            for (i in noteDetailsList.indices) {
//                if (noteDetailsList[i].getNote()
//                        .equalsIgnoreCase(noteText) && !noteDetailsList[i].getTypeOfNote()
//                        .equalsIgnoreCase("Shopping")
//                ) {
//                    isUpdateWidget = true
//                    position = i + 1
//                    val noteDetails: NoteDetails = database.getNote(position)
//                    if (database.getNote(position).getLockedOrNot() === 1) {
//                        lockedPassword = database.getNote(position).getLockPassword()
//                        lockedOrnot = database.getNote(position).getLockedOrNot()
//                        titleLocked = database.getNote(position).getLockTitle()
//                    }
//                    setDetailsToView(noteDetails)
//                    break
//                }
//            }
            if (isUpdateWidget) {
                binding!!.createNotesContent.etNoteText.setText(noteText)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.rlNote -> {
                (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .toggleSoftInput(
                        InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_IMPLICIT_ONLY
                    )
                activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }
            R.id.btnTTSstart -> {
                if (binding!!.createNotesContent.btnTTSPause.visibility == View.GONE) {
                    val toSpeak: String =
                        binding!!.createNotesContent.etNoteText.text.toString()
                    val params = Bundle()
                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, params, "UniqueID")
                    binding!!.createNotesContent.btnTTSPause.visibility = View.VISIBLE
                    binding!!.createNotesContent.btnTTSstart.visibility = View.INVISIBLE
                }
            }
            R.id.btnSpeak -> {
                startTextToSpeech()
            }
            R.id.btnTTSPause -> {
                if (binding!!.createNotesContent.btnTTSPause.visibility == View.VISIBLE) {
                    stopSpeech()
                }
            }
            R.id.fab_save -> {
                saveNoteDetails()
            }
        }
    }

    private fun saveNoteDetails() {
        if (!binding!!.createNotesContent.etNoteText.text.toString()
                .equals(StringUtilities.EMPTY_STRING, ignoreCase = true)
        ) {
            stopSpeech()
            // addNotesToDB()
        } else {
            val snackbar = Snackbar
                .make(
                    binding!!.addNoteCoordinatorLayout,
                    "Oops!! Nothing to save..",
                    Snackbar.LENGTH_SHORT
                )
            snackbar.setActionTextColor(Color.RED)
            snackbar.show()
        }
    }

    private fun getNonWidgetUI(bundle: Bundle?) {
        (bundle?.get(HomeFragment.NOTES_TYPE_KEY)?.let {
            notesType = it.toString()

            if (notesType.equals(HomeFragment.NOTES_TYPE_UPDATE, ignoreCase = true)) {
                position = bundle.get(HomeFragment.NOTES_TYPE_POSITION).toString().toInt()
                // val noteDetails: NoteDetails = database.getNote(position)
                //TODO:
                //getWidgetIdNotes = noteDetails.getWidgetId()
//                if (database.getNote(position).getLockedOrNot() === 1) {
//                    lockedPassword = database.getNote(position).getLockPassword()
//                    lockedOrnot = database.getNote(position).getLockedOrNot()
//                    titleLocked = database.getNote(position).getLockTitle()
//                }
                // setDetailsToView(noteDetails)
            }
        })
    }

    private fun displayADD() {
        val adView = binding!!.adView
        // Request for Ads
        val adRequest = AdRequest.Builder().build()
        MobileAds.initialize(requireContext(), { })
        adView.loadAd(adRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.menu_notes, menu)
        val action_alert = menu.findItem(R.id.action_alert)
        val action_font = menu.findItem(R.id.action_font)
        val action_lock = menu.findItem(R.id.action_lock)
        if (isWidgetView) {
            action_alert.isVisible = false
            action_font.isVisible = false
            action_lock.isVisible = false
            action_alert.isEnabled = false
            action_font.isEnabled = false
            action_lock.isEnabled = false
        } else {
            action_alert.isVisible = true
            action_font.isVisible = true
            action_lock.isVisible = true
            action_alert.isEnabled = true
            action_font.isEnabled = true
            action_lock.isEnabled = true
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_alert) {
            // setDate()
            return true
        }
        if (id == R.id.action_color) {
            colorOptionsPickerDialog()
            return true
        }
        if (id == R.id.action_font) {
            setFont()
            return true
        }
        if (id == R.id.action_lock) {
            // setLock()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun colorOptionsPickerDialog() {

        colorOptionsDialog =
            NotesColorOptionsDialog(requireContext(), object : DialogSelectionListener {
                override fun onDialogInputSelected(id: Int) {
                    when (id) {
                        R.id.imageOneLayout -> {
                            colorPickerDialog(getString(R.string.backgroundColor))
                            colorOptionsDialog.dismiss()
                        }
                        R.id.imageTwoLayout -> {
                            colorPickerDialog(getString(R.string.textColor))
                            colorOptionsDialog.dismiss()
                        }
                    }
                }

            })
    }

    private fun colorPickerDialog(type: String) {
        val dialog = AmbilWarnaDialog(requireContext(), color, false,
            object : AmbilWarnaDialog.OnAmbilWarnaListener {
                override fun onOk(dialog: AmbilWarnaDialog, color: Int) {
                    val colorFormat = String.format("0x%08x", color)
                    val colorCode = colorFormat.split("0xff").toTypedArray()
                    if (colorCode.isNotEmpty()) {
                        val finalColor = colorCode[1]
                        if (type.equals(getString(R.string.backgroundColor), ignoreCase = true)) {
                            colorDefault = "#$finalColor"
                            binding!!.createNotesContent.rlNote.setBackgroundColor(
                                Color.parseColor(
                                    colorDefault
                                )
                            )
                        }
                        if (type.equals(getString(R.string.textColor), ignoreCase = true)) {
                            colorTextDefault = "#$finalColor"
                            binding!!.createNotesContent.etNoteText.setTextColor(
                                Color.parseColor(
                                    colorTextDefault
                                )
                            )
                            binding!!.createNotesContent.etNoteText.setHintTextColor(
                                Color.parseColor(
                                    colorTextDefault
                                )
                            )
                            binding!!.createNotesContent.alertText.setTextColor(
                                Color.parseColor(
                                    colorTextDefault
                                )
                            )
                        }
                    }
                }

                override fun onCancel(dialog: AmbilWarnaDialog) {}
            })
        dialog.show()
    }

    private fun setFont() {
        var selectFontDialog: FontsListDialog? = null

        selectFontDialog = FontsListDialog(requireContext(), object : FontSelectionListener {
            override fun onFontSelected(fontList: ArrayList<String>, position: Int) {
                val type = Typeface.createFromAsset(context?.assets, "fonts/" + fontList[position])
                setFontToEditText(type)
                fontsSelected = fontList[position]
                selectFontDialog?.dismiss()
            }

        }, fontsSelected)
    }

    private fun setFontToEditText(type: Typeface) {
        binding!!.createNotesContent.etNoteText.textSize = 22f
        binding!!.createNotesContent.alertText.textSize = 14f
        binding!!.createNotesContent.etNoteText.typeface = type
        binding!!.createNotesContent.alertText.typeface = type
    }


    private fun startTextToSpeech() {
        stopSpeech()
        if (NetworkUtilities.isNetworkAvailable(requireContext())) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Speak now"
            )
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
            } catch (a: ActivityNotFoundException) {
                Toast.makeText(
                    activity?.applicationContext,
                    "Sorry! Your device doesn\\'t support speech input",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val snackbar = Snackbar
                .make(
                    binding!!.addNoteCoordinatorLayout,
                    "Oops!! Please connect to the Internet for voice input.",
                    Snackbar.LENGTH_SHORT
                )
            snackbar.setActionTextColor(Color.RED)
            snackbar.show()
        }
    }

    private fun stopSpeech() {
        if (tts.isSpeaking) {
            binding!!.createNotesContent.btnTTSPause.visibility = View.GONE
            binding!!.createNotesContent.btnTTSstart.visibility = View.VISIBLE
            tts.stop()
        }
    }

    private fun ttsListeners() {
        val listenerResult =
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onDone(utteranceId: String) {
                    if (utteranceId.equals("UniqueID", ignoreCase = true)) {
                        try {
                            activity?.runOnUiThread(Runnable {
                                binding!!.createNotesContent.btnTTSstart.visibility = View.VISIBLE
                                binding!!.createNotesContent.btnTTSPause.visibility = View.GONE
                            })
                        } catch (e: Exception) {
                            Log.d("error", e.toString())
                        }
                    }
                }

                override fun onError(utteranceId: String) {}
                override fun onStart(utteranceId: String) {}
            })
        if (listenerResult != TextToSpeech.SUCCESS) {
            Log.e(
                TAG, "failed to add utterance completed listener"
            )
        }
    }

    /**
     * Receiving speech input
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    if (null != result && result.size > 0) {
                        val editText = binding!!.createNotesContent.etNoteText
                        editText.setText(editText.text.toString() + result[0])
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(): CreateNotesFragment {
            return CreateNotesFragment()
        }

        const val TAG = "EditNotesView"
        private const val REQ_CODE_SPEECH_INPUT = 100
    }

    override fun onBackPressed(): Boolean {
        //TODO:
        return false
    }
}