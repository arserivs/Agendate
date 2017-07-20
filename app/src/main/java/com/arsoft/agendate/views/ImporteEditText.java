package com.arsoft.agendate.views;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * Created by larcho on 10/7/16.
 */

public class ImporteEditText extends AppCompatEditText {
    public ImporteEditText(Context context) {
        super(context);
        setup();
    }

    public ImporteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public ImporteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        this.addTextChangedListener(new ImporteTextWatcher(this));
    }

    private class ImporteTextWatcher implements TextWatcher {

        private EditText editText;
        private DecimalFormat guaraniesFormatter = new DecimalFormat("#,###.##", new DecimalFormatSymbols(new Locale("es", "PY")));

        public ImporteTextWatcher(final EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            editText.removeTextChangedListener(this);

            try {
                String text = editable.toString().replace(",", "").replace(".", "");
                Number n = guaraniesFormatter.parse(text);

                editText.setText(guaraniesFormatter.format(n));

                if(editText.getText().length() > 0) {
                    editText.setSelection(editText.getText().length());
                }
            } catch (NumberFormatException nfe) {
                // do nothing?
            } catch (ParseException e) {
                // do nothing?
            }

            editText.addTextChangedListener(this);

        }
    }
}
