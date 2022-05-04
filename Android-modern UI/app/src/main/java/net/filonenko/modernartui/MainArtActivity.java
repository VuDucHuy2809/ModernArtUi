package net.filonenko.modernartui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.AlertDialog;

public class MainArtActivity extends Activity {
    private final String TAG = "MainArtActivity";

    /// We need to keep these Views global to manipulate them from multiple methods
    View square1;
    View square2;
    View square3;
    View square4;
    View square5;


    private double square1Hue = 320; // Cyan
    private double square2Hue = 0; // Red
    private double square3Hue = 90; // Green
    //private double square4Hue = 0; // White. Keep this line alive for possible changes
    private double square5Hue = 270; // Magenta-blue



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_art);

        /// Get GUI objects
        square1 = findViewById(R.id.square1);
        square2 = findViewById(R.id.square2);
        square3 = findViewById(R.id.square3);
        square4 = findViewById(R.id.square4);
        square5 = findViewById(R.id.square5);


        /// SeekBar will be the only interface to vary colors
        SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setSquareColors(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Set predefined initial colors
        setInitialColors();
    }

    /// Shift color values according to a SeekBar
    private void setSquareColors(int percent) {
        double hueShift = percent*2;
        double value = 0.75 + (double)percent / 400;
        double saturation = 1.0 - (double)percent / 400;

        double hue1 = (square1Hue + hueShift < 360) ? square1Hue + hueShift : hueShift - (360-square1Hue);
        double hue2 = (square2Hue + hueShift < 360) ? square2Hue + hueShift : hueShift - (360-square2Hue);
        double hue3 = (square3Hue + hueShift < 360) ? square3Hue + hueShift : hueShift - (360-square3Hue);
        //double hue4 = (square4Hue + hueShift < 360) ? square4Hue + hueShift : hueShift - (360-square4Hue);
        double hue5 = (square5Hue + hueShift < 360) ? square5Hue + hueShift : hueShift - (360-square5Hue);

        square1.setBackgroundColor(hsvToRgba(hue1,saturation,value));
        square2.setBackgroundColor(hsvToRgba(hue2,saturation,value));
        square3.setBackgroundColor(hsvToRgba(hue3,saturation,value));
        square4.setBackgroundColor(hsvToRgba(0,0,0.8)); // White-gray
        square5.setBackgroundColor(hsvToRgba(hue5,saturation,value));
    }

    private void setInitialColors() {
        setSquareColors(0);
    }

    /**
     * Color space conversion is done according to equations at
     * https://en.wikipedia.org/wiki/HSL_and_HSV
     * @param hue range [0, 360)
     * @param saturation range [0, 1]
     * @param value range [0, 1]
     * @return HEX code 0xAARRGGBB where AA, RR, GG, BB are the alpha, red, green, and blue
     * channel intensities respectively
     */
    private int hsvToRgba(double hue, double saturation, double value) {
        int result;

        double c = value * saturation;
        double hPrime = hue/60;
        double x = c * ( 1 - Math.abs(hPrime%2 - 1));
        double m = value - c;

        double r1 = 0;
        double g1 = 0;
        double b1 = 0;

        /// Overlaps is working fine
        if (hPrime >= 0 && hPrime <= 1) {
            r1 = c;
            g1 = x;
            b1 = 0;
        }
        else if (hPrime >= 1 && hPrime <= 2) {
            r1 = x;
            g1 = c;
            b1 = 0;
        }
        else if (hPrime >= 2 && hPrime <= 3) {
            r1 = 0;
            g1 = c;
            b1 = x;
        }
        else if (hPrime >= 3 && hPrime <= 4) {
            r1 = 0;
            g1 = x;
            b1 = c;
        }
        else if (hPrime >= 4 && hPrime <= 5) {
            r1 = x;
            g1 = 0;
            b1 = c;
        }
        else if (hPrime >= 5 && hPrime <= 6) {
            r1 = c;
            g1 = 0;
            b1 = x;
        }

        int r = (int)((r1 + m) * 255);
        int g = (int)((g1 + m) * 255);
        int b = (int)((b1 + m) * 255);

        result = (0b11111111 << (3*8)) + (r << (2*8)) + (g << 8) + b;
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.more_info:
                showAlertDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainArtActivity.this,
                android.R.style.Theme_Material_Dialog_Alert).create();
        alertDialog.setTitle(getString(R.string.alert_top));
        alertDialog.setMessage(getString(R.string.alert_body));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.alert_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://MoMA.org"));
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.alert_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
