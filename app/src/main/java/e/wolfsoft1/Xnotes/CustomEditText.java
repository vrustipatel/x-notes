package e.wolfsoft1.Xnotes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setCustomSelectionActionModeCallback(new BlockedActionModeCallback());
        this.setLongClickable(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.setInsertionDisabled();
        }
        return super.onTouchEvent(event);
    }

    /**
    * This method sets TextView#Editor#mInsertionControllerEnabled field to false
    * to return false from the Editor#hasInsertionController() method to PREVENT showing
    * of the insertionController from EditText
    * The Editor#hasInsertionController() method is called in  Editor#onTouchUpEvent(MotionEvent event) method.
    */
    private void setInsertionDisabled() {
        try {
            Field editorField = TextView.class.getDeclaredField("mEditor");
            editorField.setAccessible(true);
            Object editorObject = editorField.get(this);

            Class editorClass = Class.forName("android.widget.Editor");
            Field mInsertionControllerEnabledField = editorClass.getDeclaredField("mInsertionControllerEnabled");
            mInsertionControllerEnabledField.setAccessible(true);
            mInsertionControllerEnabledField.set(editorObject, false);
        }
        catch (Exception ignored) {
            // ignore exception here
        }
    }

    @Override
    public boolean isSuggestionsEnabled() {
        return false;
    }

    private class BlockedActionModeCallback implements ActionMode.Callback {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }
    }
}