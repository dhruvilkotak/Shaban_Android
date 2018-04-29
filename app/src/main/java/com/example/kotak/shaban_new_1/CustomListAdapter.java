package com.example.kotak.shaban_new_1;

/**
 * Created by kotak on 03/04/2018.
 */
       import android.app.Activity;
       import android.util.Log;
       import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

       import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname;
    private final ArrayList<Integer> imgid;

    public CustomListAdapter(Activity context, ArrayList<String> itemname, ArrayList<Integer> imgid) {
        super(context, R.layout.course_textview, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.course_textview, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.courseTextview);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.item_icon);

        txtTitle.setText(itemname.get(position));
        Log.d("imgId",""+imgid.get(position));
        imageView.setImageResource(imgid.get(position));
        return rowView;

    };
}
