package com.ace.floatwindow.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ace.floatwindow.R;

/**
 * Created by JunBin on 2015/8/17.
 */
public class GeneralDialog extends Dialog {
    protected GeneralDialog(Context context) {
        super(context);
        initViews();
    }

    protected GeneralDialog(Context context, int theme) {
        super(context, theme);
        initViews();
    }

    private void initViews() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String neutralButtonText;
        private String negativeButtonText;
        private View contentView;

        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener neutralButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog message from String
         *
         * @param title
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param title
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         *
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         *
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the neutral button resource and it's listener
         *
         * @param neutralButtonText
         * @param listener
         * @return
         */
        public Builder setNeutralButton(int neutralButtonText, DialogInterface.OnClickListener listener) {
            this.neutralButtonText = (String) context.getText(neutralButtonText);
            this.neutralButtonClickListener = listener;
            return this;
        }

        /**
         * Set the neutral button text and it's listener
         *
         * @param neutralButtonText
         * @param listener
         * @return
         */
        public Builder setNeutralButton(String neutralButtonText, DialogInterface.OnClickListener listener) {
            this.neutralButtonText = neutralButtonText;
            this.neutralButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         *
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         *
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public GeneralDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final GeneralDialog dialog = new GeneralDialog(context, R.style.CustomDialog);
            View layout = inflater.inflate(R.layout.general_dialog, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            // set the dialog title
            if (title != null) {
                ((TextView) layout.findViewById(R.id.custom_dialog_tv_title)).setText(title);
            } else {
                layout.findViewById(R.id.custom_dialog_tv_title).setVisibility(View.GONE);
                layout.findViewById(R.id.custom_dialog_v_top_divider).setVisibility(View.GONE);
            }

            // set the confirm button
            if (positiveButtonText != null) {
                ((TextView) layout.findViewById(R.id.custom_dialog_tv_button3)).setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((TextView) layout.findViewById(R.id.custom_dialog_tv_button3)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.custom_dialog_tv_button3).setVisibility(View.GONE);
            }

            // set the cancel button
            if (neutralButtonText != null) {
                ((TextView) layout.findViewById(R.id.custom_dialog_tv_button2)).setText(neutralButtonText);
                if (neutralButtonClickListener != null) {
                    ((TextView) layout.findViewById(R.id.custom_dialog_tv_button2)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            neutralButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEUTRAL);
                        }
                    });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.custom_dialog_tv_button2).setVisibility(View.GONE);
                layout.findViewById(R.id.custom_dialog_v_button2_divier).setVisibility(View.GONE);
            }

            // set the cancel button
            if (negativeButtonText != null) {
                ((TextView) layout.findViewById(R.id.custom_dialog_tv_button1)).setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((TextView) layout.findViewById(R.id.custom_dialog_tv_button1)).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                    });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.custom_dialog_tv_button1).setVisibility(View.GONE);
                layout.findViewById(R.id.custom_dialog_v_button1_divier).setVisibility(View.GONE);
            }

            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.custom_dialog_tv_message)).setText(message);
            } else if (contentView != null) {
                // if no message set add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.custom_dialog_ll_container)).removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.custom_dialog_ll_container)).addView(contentView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            if (positiveButtonClickListener == null && neutralButtonClickListener == null && negativeButtonClickListener == null) {
                layout.findViewById(R.id.custom_dialog_ll_buttons).setVisibility(View.GONE);
            }

            dialog.setContentView(layout);
            return dialog;
        }
    }
}
