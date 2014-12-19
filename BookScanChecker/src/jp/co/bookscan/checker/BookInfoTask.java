package jp.co.bookscan.checker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

// actually singleton class 
class BookInfoTask extends AsyncTask<String, Void, Integer> {

    private final FragmentActivity activity;
    private String strISBN = null;
    
    private String url = null;
    
    //instance vars are class vars in singleton case
    private static String title = null;
    private static String author = null;
    private static String pc_name = null;
    private static String pc_addr = null;
    private static String pc_tel = null;
    private static String strTelNo = null;
    private static Drawable dImg = null;    
    
    private static final int RESULT_SUCCESS = 0;
    private static final int RESULT_SERVER_ERROR = 1;
    private static final int RESULT_JSON_ERROR = 2;
    private static final int RESULT_ISBN_ERROR = 3;
    
    public BookInfoTask(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Integer doInBackground(String... params) {
        strISBN = params[0];
        if (checkISBN(strISBN) == false)
            return RESULT_ISBN_ERROR;
        try {
            parseInfo(serverQuery(strISBN));
        } catch (NetworkErrorException e) {
            return RESULT_SERVER_ERROR;
        } catch (JSONException e) {
            return RESULT_JSON_ERROR;
        }
        dImg = downloadImage(url);
        return RESULT_SUCCESS;
    }

    private boolean checkISBN(String str) {
        if (str.length() != 13) return false;
        if (!str.substring(0,3).equals("978"))
            return false;
        int sum = 0;
        int[] v = new int[13];
        for (int i = 0; i < 13; i++)
            v[i] = Character.digit(str.charAt(i), 10);
        for (int i = 0; i < 12; i += 2)
            sum += v[i] + v[i+1] * 3;
        if ((v[12] + sum) % 10 == 0)
            return true;
        return false;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        if (resultCode != RESULT_SUCCESS) {
            int id_title;
            String strMsg;
            switch (resultCode) {
            case RESULT_ISBN_ERROR:
                id_title = R.string.isbn_error;
                strMsg = activity.getResources().getString(R.string.isbn_msg);
                break;
            case RESULT_SERVER_ERROR:
                id_title = R.string.server_error;
                strMsg = activity.getResources().getString(R.string.server_guide);
                break;
            case RESULT_JSON_ERROR:
                id_title = R.string.query_result;
                strMsg = activity.getResources().getString(R.string.bookinfo_notfound, strISBN);
                break;
            default:
                return;
            }
            showAlertDialog(id_title, strMsg);
            return;
        }

    	new BookInfoDialog()
    	.show(activity.getSupportFragmentManager(), "dialog");

        return;
    }

    private void showAlertDialog(int titleId, String strMsg) {
    	SimpleDialog.getNewInstance(activity.getResources().getString(titleId), strMsg)
    	.show(activity.getSupportFragmentManager(), "dialog");
    }

    public static class DialDialog extends DialogFragment {

		public Dialog onCreateDialog(Bundle state) {
			AlertDialog d = new AlertDialog.Builder(getActivity())
			.setTitle(R.string.confirm_dial)
			.setMessage(strTelNo + getActivity().getString(R.string.dial_to))
			.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					getActivity().startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+strTelNo)));
				}
			})
			.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.setCancelable(true)
			.create();
			d.setCanceledOnTouchOutside(true);  
			return d;
		}  
    }

   public static class BookInfoDialog extends DialogFragment {
		public Dialog onCreateDialog(Bundle state) {
	        LayoutInflater factory = LayoutInflater.from(getActivity());
	        int id = R.layout.bookinfo;

	        //for irregular device, galaxy tab
			if (Build.MODEL.equals("SC-01C")|| Build.MODEL.equals("GT-P1000")) {
				DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
				if (dm.widthPixels / dm.density < 480)
					id = R.layout.bookinfo_s;
			}
			
			final View bookinfoView = factory.inflate(id, null);

	        Dialog dialog = new Dialog(getActivity()); 

	        TextView tvTitle = (TextView)dialog.findViewById(android.R.id.title);
	        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 
								      LinearLayout.LayoutParams.WRAP_CONTENT));
	        FrameLayout fl = (FrameLayout)dialog.findViewById(android.R.id.content).getParent();
	        fl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 
	        		                                         LinearLayout.LayoutParams.WRAP_CONTENT));

	        dialog.setContentView(bookinfoView);
	         
	        dialog.setTitle(R.string.query_result);
	        dialog.setCanceledOnTouchOutside(true);  

	        ((TextView)bookinfoView.findViewById(R.id.text_title)).setText(title);
	        ((TextView)bookinfoView.findViewById(R.id.text_author)).setText(author);
	        ((ImageView)bookinfoView.findViewById(R.id.img_bookinfo)).setImageDrawable(dImg);

	        if (pc_name == null) {
	            bookinfoView.findViewById(R.id.table_pc).setVisibility(View.GONE);
	        } else {
	            bookinfoView.findViewById(R.id.publisher_notfound).setVisibility(View.GONE);

	            ((TextView)bookinfoView.findViewById(R.id.text_pcname)).setText(pc_name);
	            ((TextView)bookinfoView.findViewById(R.id.text_pcaddr)).setText(pc_addr);
	            ((TextView)bookinfoView.findViewById(R.id.text_pctel)).setText(pc_tel);

	            TableRow trPCTel = (TableRow)bookinfoView.findViewById(R.id.row_pctel);
	            trPCTel.setClickable(true);
	            trPCTel.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) {
	                	new DialDialog()
	                	.show(getActivity().getSupportFragmentManager(), "dialog");
	                   return;
	                }}
	            );

	        }
	        
			return dialog;
		}  
    }
    
    private String serverQuery(String strISBN) throws NetworkErrorException {
        
        HttpClient httpClient = new DefaultHttpClient();

        StringBuilder uri = new StringBuilder("http://publisher.bookscan.co.jp/amazon.php?q=" + strISBN + "&nojson=0");
        HttpGet request = new HttpGet(uri.toString());
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(request);
        } catch (Exception e) {
            throw new NetworkErrorException("Error HTTP Response");
        }
        int status = httpResponse.getStatusLine().getStatusCode();
        String data = null;

        if (HttpStatus.SC_OK == status) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(outputStream);
                data = outputStream.toString();
            } catch (IOException e) {
                Log.d("BookInfoTask", "Error");
            }
        } else {
            throw new NetworkErrorException("Bad HTTP Status Code: " + status);
        }

        return data;
    }
    
    private void parseInfo(String data) throws JSONException {
        JSONObject pc = null; //publisher contact
        JSONArray bl = new JSONObject(data).getJSONArray("booklist");
        int l = bl.length();
        for (int i = 0; i < l; i++) {
            JSONObject obj = bl.getJSONObject(i);
            url = obj.optString("URL", url);
            title = obj.optString("title", title);
            author = obj.optString("Author", author);
            if (pc == null) pc = obj.optJSONObject("Publisher_contact");
        }
        if (pc != null) {
            pc_name = pc.optString("name");
            pc_addr = pc.optString("zip") + "\n" + pc.optString("address");
            pc_tel = pc.optString("tel");
            strTelNo = pc_tel.replaceAll("-", "");
        }
        return;
    }
    
    private Drawable downloadImage(String url) {
        Log.d("URL", "URL = " + url);
        Object imgFile;
        try {
            imgFile =  new URL(url).getContent();
        } catch (MalformedURLException e) {
            Log.d("URL", "badURL = " + url);
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.d("URL", "IOError URL = " + url);

            e.printStackTrace();
            return null;
        }
        InputStream is = (InputStream) imgFile;
        return Drawable.createFromStream(is, "src");
    }
}
