package daniarachid.donation.DonationRequestManagement;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class QuantityPickerDialog extends DialogFragment {
    int max, min;
    private NumberPicker.OnValueChangeListener valueChangeListener;

    public QuantityPickerDialog(int min, int max) {
        this.max = max;
        this.min = min;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        final NumberPicker numberPicker = new NumberPicker(getActivity());

        numberPicker.setMinValue(min);
        numberPicker.setMaxValue(max);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Value");
        builder.setMessage("Choose a number :");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }

    public NumberPicker.OnValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
}
