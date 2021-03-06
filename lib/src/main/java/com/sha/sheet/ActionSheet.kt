package com.sha.sheet

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.action_sheet.*


/**
 * Created by Shaban Kamel on 26/09/20.
 */

class ActionSheet(private var options: Options) : BottomSheetDialogFragment() {
    private val adapter by lazy {
        ActionSheetAdapter(options.actions) {
            if (!options.isCancelableOnActionClick) return@ActionSheetAdapter
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SheetDialog)
    }

    @Nullable
    override fun onCreateView(
            inflater: LayoutInflater,
            @Nullable container: ViewGroup?,
            @Nullable savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.action_sheet, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val w = dialog.window
        // Hide title
        w?.requestFeature(Window.FEATURE_NO_TITLE)
        w?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setOnShowListener {
            setupPeekHeight()
            setupDragging()
        }
        return dialog
    }

    private fun setupDragging() {
        if (options.isCancelable) return

        // disable dragging

        val sheetDialog = dialog as BottomSheetDialog
        val bottomSheet = sheetDialog.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout
                ?: return
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.isDraggable = false
    }

    private fun setupPeekHeight() {
        val sheetDialog = dialog as BottomSheetDialog
        val bottomSheet = sheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet) ?: return
        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<FrameLayout?>(bottomSheet)
        bottomSheetBehavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setup()
    }

    private fun setup() {
        setupRecycler()
        setupTitle()
        setupMessage()
        setupDivider()
        setupCancel()
    }

    private fun setupRecycler() {
        val layoutManager = LinearLayoutManager(context)
        rv.layoutManager = layoutManager
        rv.adapter = adapter
        rv.addItemDecoration(SimpleDividerItemDecoration(requireContext()))
    }

    private fun setupCancel() {
        if (!options.isCancelable) {
            viewCancel.visibility = View.GONE
            dialog?.setCanceledOnTouchOutside(false)
            return
        }
        btnCancel.setOnClickListener { dismiss() }
    }

    private fun setupMessage() {
        val message = options.message
        if (message == null) {
            viewMessage.visibility = View.GONE
            return
        }
        tvMessage.text = message
    }

    private fun setupDivider() {
        if (!options.title.isNullOrBlank() || !options.message.isNullOrBlank()) {
            divider.visibility = View.VISIBLE
            topSpace.visibility = View.VISIBLE
            return
        }
        topSpace.visibility = View.GONE
        divider.visibility = View.GONE
    }

    private fun setupTitle() {
        val title = options.title
        if (title == null) {
            viewTitle.visibility = View.GONE
            return
        }
        tvTitle.text = title
    }

    class Options {
        var actions: List<ActionItem> = emptyList()
        var title: String? = null
        var message: String? = null
        var isCancelable = true
        var isCancelableOnActionClick = true
    }

    class Builder {
        private val options = Options()
        var actions: List<ActionItem> = emptyList()
        var title: String? = null
        var message: String? = null
        var isCancelable = true
        var isCancelableOnActionClick = true

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun message(message: String): Builder {
            this.message = message
            return this
        }

        fun actions(actions: List<ActionItem>): Builder {
            this.actions = actions
            return this
        }

        fun isCancelable(isCancelable: Boolean): Builder {
            this.isCancelable = isCancelable
            return this
        }

        fun isCancelableOnActionClick(isCancelable: Boolean): Builder {
            this.isCancelableOnActionClick = isCancelable
            return this
        }

        fun show(manager: FragmentManager) {
            options.title = title
            options.message = message
            options.actions = actions
            options.isCancelable = isCancelable
            options.isCancelableOnActionClick = isCancelableOnActionClick
            ActionSheet(options).show(manager)
        }
    }

    fun show(manager: FragmentManager) {
        super.show(manager, javaClass.name)
    }

    companion object {
        fun create(block: Builder.() -> Unit): Builder {
           return Builder().apply { block() }
        }
    }
}