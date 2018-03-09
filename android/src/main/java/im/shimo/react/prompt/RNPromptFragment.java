package im.shimo.react.prompt;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.EditText;
import android.view.inputmethod.EditorInfo;
import android.graphics.Color;

import javax.annotation.Nullable;

public class RNPromptFragment extends DialogFragment implements DialogInterface.OnClickListener {

    /* package */ static final String ARG_TITLE = "title";
    /* package */ static final String ARG_MESSAGE = "message";
    /* package */ static final String ARG_BUTTON_POSITIVE = "button_positive";
    /* package */ static final String ARG_BUTTON_NEGATIVE = "button_negative";
    /* package */ static final String ARG_BUTTON_NEUTRAL = "button_neutral";
    /* package */ static final String ARG_ITEMS = "items";
    /* package */ static final String ARG_TYPE = "type";
    /* package */ static final String ARG_STYLE = "style";
    /* package */ static final String ARG_DEFAULT_VALUE = "defaultValue";
    /* package */ static final String ARG_PLACEHOLDER = "placeholder";
    /* package */ static final String ARG_PLACEHOLDER_COLOR = "placeholderColor";
    /* package */ static final String ARG_DISABLE_FULL_SCREEN_UI = "disableFullscreenUI";
    /* package */ static final String ARG_HIGHLIGHT_COLOR = "highlightColor";
    /* package */ static final String ARG_COLOR = "color";
    /* package */ static final String ARG_BUTTON_COLOR = "buttonColor";

    private EditText mInputText;

    private String mButtonColor;

    public enum PromptTypes {
        TYPE_DEFAULT("default"),
        PLAIN_TEXT("plain-text"),
        SECURE_TEXT("secure-text"),
        NUMERIC("numeric"),
        EMAIL_ADDRESS("email-address"),
        PHONE_PAD("phone-pad");

        private final String mName;

        PromptTypes(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private
    @Nullable
    RNPromptModule.PromptFragmentListener mListener;

    public RNPromptFragment() {
        mListener = null;
    }

    public void setListener(@Nullable RNPromptModule.PromptFragmentListener listener) {
        mListener = listener;
    }

    public Dialog createDialog(Context activityContext, Bundle arguments) {
        AlertDialog.Builder builder;
        String style = arguments.containsKey(ARG_STYLE) ? arguments.getString(ARG_STYLE) : "default";
        style = style != null ? style : "default";

        // AlertDialog style
        switch (style) {
            case "shimo":
                builder = new AlertDialog.Builder(activityContext, R.style.ShimoAlertDialogStyle);
                break;
            default:
                builder = new AlertDialog.Builder(activityContext);
        }

        builder.setTitle(arguments.getString(ARG_TITLE));

        if (arguments.containsKey(ARG_BUTTON_POSITIVE)) {
            builder.setPositiveButton(arguments.getString(ARG_BUTTON_POSITIVE), this);
        }
        if (arguments.containsKey(ARG_BUTTON_NEGATIVE)) {
            builder.setNegativeButton(arguments.getString(ARG_BUTTON_NEGATIVE), this);
        }
        if (arguments.containsKey(ARG_BUTTON_NEUTRAL)) {
            builder.setNeutralButton(arguments.getString(ARG_BUTTON_NEUTRAL), this);
        }
        // if both message and items are set, Android will only show the message
        // and ignore the items argument entirely
        if (arguments.containsKey(ARG_MESSAGE)) {
            builder.setMessage(arguments.getString(ARG_MESSAGE));
        }

        if (arguments.containsKey(ARG_ITEMS)) {
            builder.setItems(arguments.getCharSequenceArray(ARG_ITEMS), this);
        }

        if (arguments.containsKey(ARG_BUTTON_COLOR)) {
            mButtonColor = arguments.getString(ARG_BUTTON_COLOR);
        } else {
            mButtonColor = "";
        }

        AlertDialog alertDialog = builder.create();

        // input style
        LayoutInflater inflater = LayoutInflater.from(activityContext);
        final EditText input;
        switch (style) {
            case "shimo":
                input = (EditText) inflater.inflate(R.layout.edit_text, null);
                break;
            case "cust":
                input = (EditText) inflater.inflate(R.layout.cust_edit_text, null);
                break;
            default:
                input = new EditText(activityContext);
        }



        // input type
        int type = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
        if (arguments.containsKey(ARG_TYPE)) {
            String typeString = arguments.getString(ARG_TYPE);
            if (typeString != null) {
                switch (typeString) {
                    case "secure-text":
                        type = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                        break;
                    case "numeric":
                        type = InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER;
                        break;
                    case "email-address":
                        type = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                        break;
                    case "phone-pad":
                        type = InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_PHONE;
                        break;
                    case "plain-text":
                    default:
                        type = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
                }
            }
        }
        input.setInputType(type);

        if (arguments.containsKey(ARG_HIGHLIGHT_COLOR)) {
            String highlightColor = arguments.getString(ARG_HIGHLIGHT_COLOR);
            if (highlightColor != null) {
                input.setHighlightColor(Color.parseColor(highlightColor));
            }
        }

        if (arguments.containsKey(ARG_DISABLE_FULL_SCREEN_UI)) {
            boolean disableFullscreenUI = arguments.getBoolean(ARG_DISABLE_FULL_SCREEN_UI);
            if (disableFullscreenUI) {
                int imeOptions = input.getImeOptions();
                input.setImeOptions(imeOptions | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            }
        }

        if (arguments.containsKey(ARG_DEFAULT_VALUE)) {
            String defaultValue = arguments.getString(ARG_DEFAULT_VALUE);
            if (defaultValue != null) {
                input.setText(defaultValue);
                int textLength = input.getText().length();
                input.setSelection(textLength, textLength);
            }
        }


        if (arguments.containsKey(ARG_COLOR)) {
            String color = arguments.getString(ARG_COLOR);
            if (color != null) {
                input.setTextColor(Color.parseColor(color));
            }
        }

        if (arguments.containsKey(ARG_PLACEHOLDER)) {
            input.setHint(arguments.getString(ARG_PLACEHOLDER));
            if (arguments.containsKey(ARG_PLACEHOLDER_COLOR)) {
                String placeholderColor = arguments.getString(ARG_PLACEHOLDER_COLOR);
                if (placeholderColor != null) {
                    input.setHintTextColor(Color.parseColor(arguments.getString(ARG_PLACEHOLDER_COLOR)));
                }
            }
        }
        alertDialog.setView(input, 50, 15, 50, 0);

        // input.setLinkTextColor(Color.parseColor("green"));

        mInputText = input;
        return alertDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = this.createDialog(getActivity(), getArguments());
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!mButtonColor.isEmpty()) {
            AlertDialog d = (AlertDialog) getDialog();
            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(mButtonColor));
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor(mButtonColor));
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mListener != null) {
            mListener.onConfirm(which, mInputText.getText().toString());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onDismiss(dialog);
        }
    }
}
