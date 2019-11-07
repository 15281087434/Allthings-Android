package songqiu.allthings.util;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import songqiu.allthings.iterface.IEditTextChangeListener;


/*******
 *
 *Created by ???
 *
 *???? 2019/8/19
 *
 *????
 *
 ********/
public class EditTextCheckUtil {


    //??????????????
    static IEditTextChangeListener mChangeListener;

    public static void setChangeListener(IEditTextChangeListener changeListener) {
        mChangeListener = changeListener;
    }


    /**
     * ?????????????
     * ????????????
     * ????????X?????X??????????
     */
    public static class textChangeListener{
        private TextView button;
        private EditText[] editTexts;

        public textChangeListener(TextView button){
            this.button=button;
        }
        public textChangeListener addAllEditText(EditText... editTexts){
            this.editTexts=editTexts;
            initEditListener();
            return this;
        }


        private void initEditListener() {
            for (EditText editText:editTexts){
                editText.addTextChangedListener(new textChange());
            }
        }


        /**
         * edit???????????????
         */
        private class textChange implements TextWatcher {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (checkAllEdit()){
                    Log.i("TAG", "??edittext???");
                    mChangeListener.textChange(true);
                    button.setEnabled(true);
                }else {
                    button.setEnabled(false);
                    Log.i("TAG", "?edittext???");
                    mChangeListener.textChange(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        }

        /**
         * ?????edit???????
         * @return
         */
        private boolean checkAllEdit() {
            for (EditText editText:editTexts){
                if (!TextUtils.isEmpty(editText.getText() + "")){
                    continue;
                }else {
                    return false;
                }
            }
            return true;
        }
    }
}

