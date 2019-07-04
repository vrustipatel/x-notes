//package e.wolfsoft1.Xnotes;
//
//import android.content.Context;
//import android.support.v7.widget.AppCompatEditText;
//
//public class MyEditText extends AppCompatEditText {
//    public MyEditText(Context context) {
//        super(context);
//    }
//
//    private int mPreviousCursorPosition;
//        @Override
//    protected void onSelectionChanged(int selStart, int selEnd) {
//        CharSequence text = getText();
//        if (text != null) {
//            if (selStart != selEnd) {
//                setSelection(mPreviousCursorPosition, mPreviousCursorPosition);
//                return;
//            }
//        }
//
//        mPreviousCursorPosition = selStart;
//        super.onSelectionChanged(selStart, selEnd);
//    }
//}