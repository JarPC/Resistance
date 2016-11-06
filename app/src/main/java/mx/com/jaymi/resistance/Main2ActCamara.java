package mx.com.jaymi.resistance;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Main2ActCamara extends AppCompatActivity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2{
    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mRgba;
    private Scalar mBlobColorHsv;
    private Scalar mBlobColorRgba;
    TextView touch_coordinates;
    EditText touch_color;
    Button cal;

    double x=-1;
    double y= -1;
    String[ ] nombre = new String[3];

    private Camera camera;



    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this){
        @Override
        public void onManagerConnected (int status){
            switch ( status){
                case LoaderCallbackInterface.SUCCESS:{
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(Main2ActCamara.this);
                }
                break;
                default:{
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2_act_camara);

        Toast.makeText(getApplicationContext(), "Posiciona el telefono de forma horizontal",
                Toast.LENGTH_LONG).show();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        touch_coordinates = (TextView) findViewById(R.id.touch_coordinates);
        touch_color= (EditText) findViewById(R.id.touch_color);





        FloatingActionButton fabon = (FloatingActionButton) findViewById(R.id.fabon);
        fabon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //encender flash

                camera = Camera.open();
                Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                camera.startPreview();

                /*Intent intent = new Intent(Main2ActCamara.this, MainActivity.class);
                startActivity(intent);*/

                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        FloatingActionButton fabofff = (FloatingActionButton) findViewById(R.id.fabofff);
        fabofff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // apagar el flash.
                Parameters parametros = camera.getParameters();
                parametros.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(parametros);
                camera.stopPreview();
                camera.release();


                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
        FloatingActionButton fabre = (FloatingActionButton) findViewById(R.id.fabre);
        fabre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2ActCamara.this, MainActivity.class);
                startActivity(intent);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mOpenCvCameraView=(CameraBridgeViewBase)findViewById(R.id.opencv_app_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        }else{
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mOpenCvCameraView !=null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onPause(){
        super.onPause();
        if (mOpenCvCameraView !=null)
            mOpenCvCameraView.disableView();
    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mBlobColorRgba=new Scalar(255);
        mBlobColorHsv= new Scalar(255);
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        return mRgba;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {


        int cols = mRgba.cols();
        int rows = mRgba.rows();

        double ylow = (double) mOpenCvCameraView.getHeight() * 0.2401961;
        double yHigh = (double) mOpenCvCameraView.getHeight() * 0.7696878;

        double xScale = (double) cols / (double) mOpenCvCameraView.getWidth();
        double yScale = (double) rows / (yHigh - ylow);


        x = motionEvent.getX();
        y = motionEvent.getY();

        y = y - ylow;
        x = x * xScale;
        y = y * yScale;

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;


        touch_coordinates.setText("X: " + Double.valueOf(x) + ", Y: " + Double.valueOf(y));

        Rect touchedRect = new Rect();

        touchedRect.x = (int) x;
        touchedRect.y = (int) y;

        touchedRect.width = 3;
        touchedRect.height = 3;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);
        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width * touchedRect.height;


        EditText cajaUno = (EditText) findViewById(R.id.touch_color);
        // EditText cajaDos = (EditText) findViewById(R.id.touch_color2);
        //EditText cajaTres = (EditText) findViewById(R.id.touch_color3);


        for (int i = 0; i < nombre.length; i++) {
            mBlobColorHsv.val[i] /= pointCount;

            mBlobColorRgba = convertScalarHsv2Rgba(mBlobColorHsv);


            cajaUno.setText(("Color: #" + String.format("%02X", (int) mBlobColorRgba.val[0]))
                    + String.format("%02X", (int) mBlobColorRgba.val[1])
                    + String.format("%02X", (int) mBlobColorRgba.val[2])
            );



        }
        cajaUno.setTextColor(Color.rgb((int) mBlobColorRgba.val[0],
                (int) mBlobColorRgba.val[1],
                (int) mBlobColorRgba.val[2]));

        nombre[0] = cajaUno.getText().toString();

        Toast.makeText(getApplicationContext(),   cajaUno.getText().toString(),
                Toast.LENGTH_SHORT).show();

        nombre[1] = cajaUno.getText().toString();

        Toast.makeText(getApplicationContext(),   cajaUno.getText().toString(),
                Toast.LENGTH_SHORT).show();

        nombre[2] =cajaUno.getText().toString();

        Toast.makeText(getApplicationContext(),   cajaUno.getText().toString(),
                Toast.LENGTH_SHORT).show();


       /* nombre[0] = cajaUno.toString();
        nombre[1] = cajaDos.toString();
        nombre[2] = cajaTres.toString();*/
        cal = (Button) findViewById(R.id.calcularr);
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                EditText cajaUno = (EditText) findViewById(R.id.touch_color);
                nombre[0] = cajaUno.getText().toString();
                nombre[1] = cajaUno.getText().toString();
                nombre[2] = cajaUno.getText().toString();
                String q =   cajaUno.getText().toString();
                String w =  cajaUno.getText().toString();
                String e = cajaUno.getText().toString();
                String a = "#8F6D40";
                String b = "#010200";
                String c = "#881D17";

                if (q.equals(a) && w.equals(b) && e.equals(c)) {

                    Toast.makeText(getApplicationContext(), "10 Kohms",
                            Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getApplicationContext(), "No coincide",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });






        return false;
    }

    private Scalar convertScalarHsv2Rgba (Scalar hsvColor){
        Mat pointMatRgba=new Mat();
        Mat pointMatHsv = new Mat(1,1,  CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);
        return new Scalar(pointMatRgba.get(0,0));
    }

}
