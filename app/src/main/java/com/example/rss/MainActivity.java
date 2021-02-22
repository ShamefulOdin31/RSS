package com.example.rss;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    Toolbar myToolbar;
    ArrayList<EuroGamerItems> euroGamerList;
    URL url1 = null;
    boolean def = true;
    boolean swap = false;
    boolean dark = false;
    boolean size = false;
    private SharedPreferences sharedPreferences;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("general", MODE_PRIVATE);

        swap = sharedPreferences.getBoolean("swap", false);
        dark = sharedPreferences.getBoolean("dark", false);
        size = sharedPreferences.getBoolean("size", false);

        myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), description.class);
                intent.putExtra("title", euroGamerList.get(i).getTitle());
                intent.putExtra("desc", euroGamerList.get(i).getDesc().replaceAll("\\<[^>]*>",""));
                intent.putExtra("pubDate", euroGamerList.get(i).getPubDate());
                intent.putExtra("link", euroGamerList.get(i).getLink());
                startActivity(intent);
            }
        });

        parseRSS();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean returnValue = false;
        switch (item.getItemId()) {
            case R.id.euro:
                rssSelector("https://www.eurogamer.net/?format=rss");
                returnValue = true;
                break;

            case R.id.nintendo:
                rssSelector("https://www.nintendolife.com/feeds/latest");
                returnValue = true;
                break;

            case R.id.settings:
                Intent intent = new Intent(this, settings.class);
                startActivityForResult(intent, 0);
        }
        return returnValue;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            boolean test = data.getBooleanExtra("swap", false);
            if(swap != test){
                swap = test;
                parseRSS();
            }

            dark = data.getBooleanExtra("dark", false);

            ListView l = findViewById(R.id.list_view);
            if(dark){

                l.setBackgroundColor(Color.GRAY);
            } else{
                l.setBackgroundColor(Color.WHITE);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void rssSelector(String url){
        try {
            url1 = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        def = false;
        parseRSS();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class RSS extends AsyncTask{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = null;

            try{
                saxParser = saxParserFactory.newSAXParser();
            }catch (ParserConfigurationException e){
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            if(def){
                try {
                    if(!swap){
                        url1 = new URL("https://www.eurogamer.net/?format=rss");
                    }
                    else{
                        url1 = new URL("https://www.nintendolife.com/feeds/latest");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            InputStream inputStream = null;

            try{
                inputStream = url1.openStream();
            } catch (IOException e){
                e.printStackTrace();
            }
            RSSHandler rssHandler = new RSSHandler();
            try{
                saxParser.parse(inputStream, rssHandler);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            listView = findViewById(R.id.list_view);

            ArrayList<String> test = new ArrayList<>();
            for(int i = 0; i < euroGamerList.size(); i++){
                test.add(euroGamerList.get(i).getTitle());
            }

            Adapter adapter = new Adapter(getApplicationContext(), R.layout.adapter_view_layout, euroGamerList);
            listView.setAdapter(adapter);

            //ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, test);
            //listView.setAdapter(arrayAdapter);
        }
    }

    public class Adapter extends ArrayAdapter<EuroGamerItems>{

        int mResource;
        private Context mContext;
        public Adapter(@NonNull Context context, int resource, @NonNull ArrayList<EuroGamerItems> objects) {
            super(context, resource, objects);
            mResource = resource;
            mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            String title = getItem(position).getTitle();
            String desc = getItem(position).getDesc().replaceAll("\\<[^>]*>","");
            String pubDate = getItem(position).getPubDate();
            String link = getItem(position).getLink();

            EuroGamerItems euroGamerItems = new EuroGamerItems(title, desc, pubDate, link);
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.adapter_view_layout, parent, false);

            TextView titleText = convertView.findViewById(R.id.textView1);
            TextView descText = convertView.findViewById(R.id.textView2);

            titleText.setText(title);
            descText.setText(pubDate);

            return convertView;
        }
    }

    public void parseRSS(){
        RSS rss = new RSS();
        rss.execute();
    }

    class EuroGamerItems{
        private String title;
        private String desc;
        private String pubDate;
        private String link;

        public EuroGamerItems(String title, String desc, String pubDate, String link){
            this.title = title;
            this.desc = desc;
            this.pubDate = pubDate;
            this.link = link;
        }

        public String getTitle(){
            return this.title;
        }

        public String getDesc(){
            return this.desc;
        }

        public String getPubDate(){
            return this.pubDate;
        }

        public String getLink() { return this.link; }
    }

    class RSSHandler extends DefaultHandler{
        boolean inTitle, inItem, inDesc, inPubdate, inLink;
        StringBuilder stringBuilder;
        StringBuilder stringBuilder2;
        StringBuilder stringBuilder3;
        StringBuilder stringBuilder4;


        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            euroGamerList = new ArrayList<>(25);
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            int i = 0;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if(qName.equals("item")){
                inItem = true;
            } else if(qName.equals("title")){
                inTitle = true;
                stringBuilder = new StringBuilder(50);
            } else if(qName.equals("description")) {
                inDesc = true;
                stringBuilder2 = new StringBuilder(50);
            } else if(qName.equals("pubDate")) {
                inPubdate = true;
                stringBuilder3 = new StringBuilder(50);
            } else if(qName.equals("link")){
                inLink = true;
                stringBuilder4 = new StringBuilder(50);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if(qName.equals("item")){
                inItem = false;
            } else if(inItem && qName.equals("pubDate")) {
                inTitle = false;
                euroGamerList.add(new EuroGamerItems(stringBuilder.toString(), stringBuilder2.toString(), stringBuilder3.toString(), stringBuilder4.toString()));
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);

            if(inItem && inTitle){
                stringBuilder.append(ch, start, length);
                inTitle = false;
            } else if(inItem && inDesc) {
                stringBuilder2.append(ch, start, length);
                inDesc = false;
            } else if(inItem && inPubdate){
                stringBuilder3.append(ch, start, length);
                inPubdate = false;
            } else if(inItem && inLink){
                stringBuilder4.append(ch, start, length);
                inLink = false;
            }
        }
    }
}

