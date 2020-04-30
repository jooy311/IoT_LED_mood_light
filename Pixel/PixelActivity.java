package com.example.eunaecho.gproject.Pixel;

        import android.app.Activity;
        import android.bluetooth.BluetoothSocket;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.drawable.ColorDrawable;
        import android.graphics.drawable.GradientDrawable;
        import android.net.Uri;
        import android.os.Environment;
        import android.support.v4.content.ContextCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.MotionEvent;
        import android.view.SurfaceView;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.eunaecho.gproject.MainActivity;
        import com.example.eunaecho.gproject.R;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.List;

        import static android.graphics.Bitmap.createBitmap;

public class PixelActivity extends AppCompatActivity {

    private int currentColor;       //현재 선택된 색상
    private Color current;          ///현재 선택 색(은애가)
    private Button colorButtons[];   //색상 버튼
    private int colors[];

    //픽셀 하나하나 위치 값 얻어가기
    ArrayList<View> pixel_position;
    int pixel_x;
    int pixel_y;

    private ActionBarDrawerToggle drawerToggle;
    private final ArrayList<DrawerMenuItem> listMenuItem = new ArrayList<>();

    private SharedPreferences settings;
    private boolean grid;

    LinearLayout paper;

    private static final String SETTINGS_GRID = "grid";
    private static final String URL_ABOUT = "https://github.com/RodrigoDavy/PixelArtist/blob/master/README.md";

    //이미지 파일 저장
    final static String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PixelFile";

    //이미지 파일 텍스트 파일 저장
    final static String pixelfoldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/PixelFile";

    //비트맵으로 변경 확인
    boolean makeBmp = false;

    //비트맵 그릴
    ArrayList<View> bit;
    Canvas canvas;

    //픽셀 데이터 통신
    // 스마트폰과 페어링 된 디바이스간 통신 채널에 대응 하는 BluetoothSocket
    static BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);


        //소켓 통신을 위해 Main소켓과 동일
        mSocket = MainActivity.mSocket;
        mOutputStream = MainActivity.mOutputStream;

        //픽셀
        paper = findViewById(R.id.paper_linear_layout);
        pixel_position = new ArrayList<View>();

        bit = new ArrayList<View>();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                updateDrawerHeader();
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        ListView leftDrawer = findViewById(R.id.left_drawer);

        addDrawerItems();


        //메뉴 설정하기
        DrawerMenuItemAdapter adapter = new DrawerMenuItemAdapter(this, listMenuItem);
        leftDrawer.setAdapter(adapter);

        leftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listMenuItem.get(i).execute();
            }
        });

        //팔레트와 픽셀 초기화
        initPalette();
        initPixels();

        settings = getPreferences(0);
        grid = settings.getBoolean(SETTINGS_GRID, true);

        if (!grid) {
            grid = true;
            pixelGrid();
        }

        openFile(".tmp", false);

        paper.setOnTouchListener(new  View.OnTouchListener() {
            public boolean onTouch (View v, MotionEvent event){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_UP:
                }
                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveFile(".tmp", false);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(SETTINGS_GRID, grid);
        editor.apply();
    }

    //Applying changes made in the ColorSelector activity
    //RGB 색상 변환하는거?
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Button b = findViewById(data.getIntExtra("id", 0));
                GradientDrawable gd = (GradientDrawable) b.getBackground();
                int c = data.getIntExtra("color", 0);
                gd.setColor(c);

                colors[data.getIntExtra("position", 0)] = c;

                if (data.getBooleanExtra("currentColor", false)) {
                    currentColor = c;
                    findViewById(R.id.palette_linear_layout).setBackgroundColor(currentColor);
                    Toast.makeText(getApplicationContext(), "색깔변함", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void addDrawerItems() {
        DrawerMenuItem drawerNew = new DrawerMenuItem(R.drawable.menu_new, R.string.menu_new) {
            @Override
            public void execute() {
                final AlertDialog alertDialog = new AlertDialog.Builder(PixelActivity.this).create();
                final View v = findViewById(R.id.color_button_1);

                alertDialog.setTitle(getString(R.string.alert_dialog_title_new));
                alertDialog.setMessage(getString(R.string.alert_dialog_message_new));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fillScreen(ContextCompat.getColor(PixelActivity.this, R.color.color_1));
                                updateDrawerHeader();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        };

        DrawerMenuItem drawerOpen = new DrawerMenuItem(R.drawable.menu_open, R.string.menu_open) {
            @Override
            public void execute() {
                File path = getExternalFilesDir(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PixelFile");

                if ((path != null) && (path.listFiles().length > 0)) {
                    File[] files = path.listFiles();

                    List<CharSequence> list = new ArrayList<>();

                    AlertDialog.Builder builder = new AlertDialog.Builder(PixelActivity.this);
                    builder.setTitle(R.string.menu_open);

                    for (File file : files) {
                        if (file.getName().contains(".txt")) {
                            list.add(0, file.getName().replace(".txt", ""));
                        }
                    }

                    final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);

                    builder.setItems(charSequences, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            openFile(charSequences[i].toString() + ".txt", true);
                            updateDrawerHeader();
                        }
                    });
                    builder.show();
                } else {
                    Toast toast = Toast.makeText(PixelActivity.this, R.string.no_files_found, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        };

        DrawerMenuItem drawerSave = new DrawerMenuItem(R.drawable.menu_save, R.string.menu_save) {
            @Override
            public void execute() {
                final AlertDialog alertDialog = new AlertDialog.Builder(PixelActivity.this).create();
                LayoutInflater layoutInflater = PixelActivity.this.getLayoutInflater();

                alertDialog.setTitle(getString(R.string.menu_save));
                alertDialog.setView(layoutInflater.inflate(R.layout.dialog_save, null));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                EditText editText = alertDialog.findViewById(R.id.dialog_filename_edit_text);
                                String filename = null;
                                if (editText != null) {
                                    filename = editText.getText().toString();
                                }

                                saveFile(filename + ".png", true);

                                //픽셀 내용 텍스트 파일로 저장
                                String contents = "";
                                View nowView;
                                int n=0;
                                for(int a=0; a<8; a++) {
                                    for(int b=0; b<8; b++){
                                        nowView = bit.get(n);
                                        ColorDrawable cd = (ColorDrawable)  nowView.getBackground();
                                        int back = cd.getColor();

                                        int cr = Color.red(back);
                                        int cg = Color.green(back);
                                        int cb = Color.blue(back);

                                        contents = contents +  String.format("%02d", n) + String.format("%03d", cr) + String.format("%03d", cg) + String.format("%03d", cb) + "\n";

                                        n++;

                                    }
                                }
                                pixelToFile(bit, pixelfoldername, filename + ".txt", contents);

                                Toast.makeText(getApplicationContext(), "파일 저장", Toast.LENGTH_SHORT).show();

                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        };

        listMenuItem.add(drawerNew);
        listMenuItem.add(drawerOpen);
        listMenuItem.add(drawerSave);
    }

    //메뉴나타내는 부분
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_options_menu, menu);
        return true;
    }

    //내가 메뉴선택하는 것
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        final AlertDialog alertDialog = new AlertDialog.Builder(PixelActivity.this).create();

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_fill:
                alertDialog.setTitle(getString(R.string.alert_dialog_title_fill));
                alertDialog.setMessage(getString(R.string.alert_dialog_message_fill));
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fillScreen(currentColor);
                                updateDrawerHeader();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

                return true;
            case R.id.menu_grid:
                pixelGrid();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //저장 되어있는 파일
    public void openFile(String fileName, boolean showToast) {
        File imageFolder = getExternalFilesDir(Environment.DIRECTORY_DCIM+"/Camera");
        File openFile = new File(imageFolder, fileName);

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(openFile));
            int color;
            String value;

            int x, y;

            if ((value = bufferedReader.readLine()) != null) {
                x = Integer.valueOf(value);
            } else {
                throw new IOException();
            }

            if ((value = bufferedReader.readLine()) != null) {
                y = Integer.valueOf(value);
            } else {
                throw new IOException();
            }

            LinearLayout linearLayout = findViewById(R.id.paper_linear_layout);

            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {

                    if ((value = bufferedReader.readLine()) != null) {
                        color = Integer.valueOf(value);
                    } else {
                        throw new IOException();
                    }

                    View v = ((LinearLayout) linearLayout.getChildAt(i)).getChildAt(j);
                    v.setBackgroundColor(color);
                }
            }

            if (showToast) {
                Toast toast = Toast.makeText(this, R.string.file_opened, Toast.LENGTH_SHORT);
                toast.show();
            }

        } catch (FileNotFoundException e) {
            Log.e("PixelActivity.openFile", "File not found");
            if (showToast) {
                Toast toast = Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_LONG);
                toast.show();
            }
        } catch (IOException e) {
            Log.e("PixelActivity.openFile", "Could not open file");
            if (showToast) {
                Toast toast = Toast.makeText(this, R.string.could_not_open, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public void saveFile(String fileName, boolean showToast) {
        if (!isExternalStorageWritable()) {
            Log.e(PixelActivity.class.getName(), "External Storage is not writable");
        }

        File imageFolder = getExternalFilesDir(Environment.DIRECTORY_DCIM + "/Camera");
        File saveFile = new File(imageFolder, fileName);

        try {
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(saveFile);
            fileWriter.append("8\n8\n");

            LinearLayout linearLayout = findViewById(R.id.paper_linear_layout);

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    View v = ((LinearLayout) linearLayout.getChildAt(i)).getChildAt(j);
                    int color = ((ColorDrawable) v.getBackground()).getColor();
                    fileWriter.append(String.valueOf(color));
                    fileWriter.append("\n");

                    bit.add(v);
                }
            }
            viewToBitmap(bit, fileName);

            fileWriter.flush();
            fileWriter.close();

            if (showToast) {
                Toast toast = Toast.makeText(this, R.string.toast_saved, Toast.LENGTH_SHORT);
                toast.show();
            }
        } catch (IOException e) {
            Log.e("PixelActivity.saveFile", "File not found");

            if (showToast) {
                Toast toast = Toast.makeText(this, R.string.toast_not_saved, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

/****여기 안됨****/
    //이미지 파일 비트맵으로 저장하기
    //이미지 파일 저장
    //이미지 파일 비트맵으로 변경
    public void viewToBitmap(ArrayList<View> view, String fileName) {

        //비트맵으로 변경 확인
        boolean makeBmp = false;
        Bitmap bitmap = null;

        canvas = new Canvas();

        for(int a=0; a<8; a++){
            for(int b=0; b<8; b++){
                bitmap = Bitmap.createBitmap(view.get(a).getWidth(), view.get(b).getHeight(), Bitmap.Config.ARGB_8888);
                canvas.drawBitmap(bitmap, a, b, null);
                if((a==8)&&(b==8))
                    makeBmp = true;
            }
        }
        //Bitmap bitmap = Bitmap.createBitmap(view.get(1).getWidth() * 64, view.get(1).getHeight() * 64, Bitmap.Config.ARGB_8888);
        //Canvas canvas = new Canvas(bitmap);

            //view.draw(canvas);

        if (makeBmp == true) {
            try {
                File dir = new File(foldername);

                //디렉토리 폴더가 없으면 생성함
                if (!dir.exists()) {
                    dir.mkdir();
                }

                FileOutputStream fos = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/" + fileName);

                int quality = 100;

                //이미지 저장 부분
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);

                //이미지 스캐닝
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/" + fileName+ ".JPEG")));


                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //픽셀 데이터 위치, 색 텍스트파일로 저장하기
    public void pixelToFile(ArrayList<View> v, String pixelfoldername, String filename, String contents){
            try{
                File dir = new File(foldername);

                //디렉토리 폴더가 없으면 생성함
                if(!dir.exists()){
                    dir.mkdir();
                }

                //파일 output stream 생성
                FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
                //파일쓰기
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                writer.write(contents);
                writer.flush();

                writer.close();
                fos.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }

    }

    /*
    public void screenShot(View view, String filename) {


        if (!checkWriteExternalPermission()) {
            Toast toast = Toast.makeText(this, R.string.no_write_permission, Toast.LENGTH_LONG);
            toast.show();

            return;
        }

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);


        if (!isExternalStorageWritable()) {
            Log.e(PixelActivity.class.getName(), "External Storage is not writable");
        }

        File imageFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));

        boolean success = true;

        if (!imageFolder.exists()) {
            success = imageFolder.mkdirs();
        }

        if (success) {
            File imageFile = new File(imageFolder, filename);

            FileOutputStream outputStream;

            try {

                if (!imageFile.exists()) {
                    imageFile.createNewFile();
                }

                outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();

                openScreenshot(imageFile);
            } catch (FileNotFoundException e) {
                Log.e(PixelActivity.class.getName(), "File not found");
            } catch (IOException e) {
                Log.e(PixelActivity.class.getName(), "IOException related to generating bitmap file");
            }
        } else {
            Toast toast = Toast.makeText(this, R.string.toast_could_not_create_app_folder, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }*/

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private boolean checkWriteExternalPermission() {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = this.checkCallingPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void updateDrawerHeader() {
        View view = findViewById(R.id.paper_linear_layout);
        Bitmap bitmap = createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        ImageView header = findViewById(R.id.drawer_header);
        header.setImageBitmap(bitmap);
    }

    //팔레트 초기화
    private void initPalette() {
        colorButtons = new Button[]{
                findViewById(R.id.color_button_0),
                findViewById(R.id.color_button_1),
                findViewById(R.id.color_button_2),
                findViewById(R.id.color_button_3),
                findViewById(R.id.color_button_4),
                findViewById(R.id.color_button_5),
                findViewById(R.id.color_button_6),
                findViewById(R.id.color_button_7),
                findViewById(R.id.color_button_8),
                findViewById(R.id.color_button_9)
        };

        colors = new int[]{
                ContextCompat.getColor(this, R.color.color_0),
                ContextCompat.getColor(this, R.color.color_1),
                ContextCompat.getColor(this, R.color.color_2),
                ContextCompat.getColor(this, R.color.color_3),
                ContextCompat.getColor(this, R.color.color_4),
                ContextCompat.getColor(this, R.color.color_5),
                ContextCompat.getColor(this, R.color.color_6),
                ContextCompat.getColor(this, R.color.color_7),
                ContextCompat.getColor(this, R.color.color_8),
                ContextCompat.getColor(this, R.color.color_9)
        };

        for (int i = 0; i < colorButtons.length; i++) {

            GradientDrawable cd = (GradientDrawable) colorButtons[i].getBackground();
            cd.setColor(colors[i]);

            colorButtons[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int n = 0;

                    for (Button b : colorButtons) {
                        if (view.getId() == b.getId()) {
                            break;
                        }

                        n += 1;
                    }

                    Intent i = new Intent(PixelActivity.this, ColorSelector.class);
                    i.putExtra("id", view.getId());
                    i.putExtra("position", n);
                    i.putExtra("color", colors[n]);

                    if (colors[n] == currentColor) {
                        i.putExtra("currentColor", true);
                    } else {
                        i.putExtra("currentColor", false);
                    }
                    startActivityForResult(i, 1);

                    return false;
                }
            });
        }

        selectColor(colorButtons[0]);
    }

    //픽셀 초기화
    private void initPixels() {
        //픽셀 판 : activity_pixel = paper_leaner_layout
        // LinearLayout paper = findViewById(R.id.paper_linear_layout);

//        int order = 1;


        for (int i = 0; i < paper.getChildCount(); i++) {
            final LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for (int j = 0; j < l.getChildCount(); j++) {
                final View pixel = l.getChildAt(j);

                //위치 알기 위해서, ArrayList는 순서를 가짐
                pixel_position.add(pixel);
//                order++;

                //픽셀을 길게 누르면 해당 픽셀의 색이 적용됨
                pixel.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        selectColor(((ColorDrawable) view.getBackground()).getColor());
                        return false;
                    }
                });
            }
        }
    }

    //Shows or hides the pixels boundaries from the paper_linear_layout
    private void pixelGrid() {
        // LinearLayout paper = findViewById(R.id.paper_linear_layout);

        int x;
        int y;

        //grid = boolean
        if (grid) {
            x = 0;
            y = 0;
        } else {
            x = 1;
            y = 1;
        }

        grid = !grid;

        for (int i = 0; i < paper.getChildCount(); i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for (int j = 0; j < l.getChildCount(); j++) {
                View pixel = l.getChildAt(j);

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) pixel.getLayoutParams();

                layoutParams.setMargins(x, y, 0, 0);
                pixel.setLayoutParams(layoutParams);
            }
        }
    }

    //Fills paper_linear_layout with chosen color
    //정한 색으로 64개의 모든 픽셀을 칠할 때,
    private void fillScreen(int color) {

        for (int i = 0; i < paper.getChildCount(); i++) {
            LinearLayout l = (LinearLayout) paper.getChildAt(i);

            for (int j = 0; j < l.getChildCount(); j++) {
                View pixel = l.getChildAt(j);

                pixel.setBackgroundColor(color);
            }
        }
        //RGB 숫자 3글자로 맞추기
        int ir =  Color.red(color);
        int ig = Color.green(color);
        int ib = Color.blue(color);

        String rgb = String.format("%03d", ir) + String.format("%03d", ig) + String.format("%03d", ib);

        Toast.makeText(getApplicationContext(), rgb + "65", Toast.LENGTH_SHORT).show();
        sendData( rgb + "65" );
    }

    //On click method that selects the current color based on the pallete button pressed
    //색깔 선택하기
    public void selectColor(View v) {
        int i = 0;

        for (Button b : colorButtons) {
            if (v.getId() == b.getId()) {
                break;
            }

            i += 1;
        }

        selectColor(colors[i]);
    }

    //Sets the current color based on the "color" argument
    public void selectColor(int color) {
        currentColor = color;

        findViewById(R.id.palette_linear_layout).setBackgroundColor(currentColor);
    }


    //픽셀 클릭 시 색깔 바뀌는 부분
    //style 에 onClick 되어있음
    public void changeColor(View v) {
        v.setBackgroundColor(currentColor);


        //내가 클릭한 픽셀을 ArrayList속 픽셀과 비교하여 인덱스를 알아내서색깔 바꾸는거 가능하게 함함
       int index = 0;
        //자바는 (a,b,g,r) 으로 레드와 블루 바꿔야 색상이 제대로 나옴? 구라같아
        //int convertedColor = Color.argb(255, Color.blue(currentColor), Color.green(currentColor), Color.red(currentColor));
        for(int a=0; a<64; a++){
            if(v == pixel_position.get(a)){
                index = a;
            }
        }

        //RGB 숫자 3글자로 맞추기
        int ir =  Color.red(currentColor);
        int ig = Color.green(currentColor);
        int ib = Color.blue(currentColor);

        String rgb = String.format("%03d", ir) + String.format("%03d", ig) + String.format("%03d", ib);
        String sIndex =  String.valueOf(index);

        // 문자열 전송하는 함수(쓰레드 사용 x)
        sendData( rgb + sIndex);
    }

    void sendData(String msg) {
        msg += "\n";  // 문자열 종료표시 (\n)
        try {
            // getBytes() : String을 byte로 변환
            // OutputStream.write : 데이터를 쓸때는 write(byte[]) 메소드를 사용함. byte[] 안에 있는 데이터를 한번에 기록해 준다.
            MainActivity.mOutputStream.write(msg.getBytes());  // 문자열 전송.
        } catch (Exception e) {  // 문자열 전송 도중 오류가 발생한 경우
            Toast.makeText(getApplicationContext(), "픽셀 데이터 전송중 오류가 발생", Toast.LENGTH_LONG).show();
            finish();  // App 종료
        }
    }

    //이미지 불러올 경우 나타내기
    void sendAllData(String msg) {
        msg += "\n";  // 문자열 종료표시 (\n)
        try {
            // getBytes() : String을 byte로 변환
            // OutputStream.write : 데이터를 쓸때는 write(byte[]) 메소드를 사용함. byte[] 안에 있는 데이터를 한번에 기록해 준다.
            MainActivity.mOutputStream.write(msg.getBytes());  // 문자열 전송.
        } catch (Exception e) {  // 문자열 전송 도중 오류가 발생한 경우
            Toast.makeText(getApplicationContext(), "픽셀 데이터 전송중 오류가 발생", Toast.LENGTH_LONG).show();
            finish();  // App 종료
        }
    }
}
