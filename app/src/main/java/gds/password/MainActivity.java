package gds.password;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.client.HttpClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnTranslate;
    private Button btnBaidu;
    private TextView tvtRes;
    private TextView tvtBaidu;
    private EditText edi;
    private EditText ediBaidu;

    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            myDebug((String)msg.obj+"11111111111");
           tvtBaidu.setText((String)msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvtRes = (TextView) findViewById(R.id.tvt_res);
        tvtBaidu  = (TextView) findViewById(R.id.tvt_baidu);
        edi = (EditText) findViewById(R.id.edi);
        ediBaidu = (EditText) findViewById(R.id.edi_baidu);
        btnTranslate = (Button) findViewById(R.id.btn_translate);
        btnBaidu  = (Button) findViewById(R.id.btn_baidu);
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEdi = edi.getText().toString();
                String[] strings = strEdi.split(" ");
                StringBuilder sbRes =new StringBuilder();
                for (int k=0; k<strings.length;k++){
                    sbRes.append(translateEachWord(strings[k])).append("  ");
                }
                tvtRes.setText("密码为:\n"+sbRes.toString());
            }
        });

        btnBaidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BaiduTranslate translate = new BaiduTranslate(ediBaidu.getText().toString(),"zh","en",handler);
                    Thread th =new Thread(translate);
                    th.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }//onCreate

    private String translateEachWord(String tewWord){
        String res = null;
        ArrayList<String> listTemp = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        char[] chs = tewWord.toCharArray();
        for(int i=0;i<chs.length;i++){
            String strBinary =   Integer.toBinaryString(chs[i]);
            res= strBinary.replace('1','x').replace('0','o');
            myDebug(chs[i] +":"+strBinary+":"+res);
            listTemp.add(res);
        }

        for ( int j=0; j<listTemp.size();j++){
            sb.append(listTemp.get(j)).append(" ");
        }
        return sb.toString();
    }

    static void myDebug(String msg){
        Log.d("tag",msg);
    }

}//Class
