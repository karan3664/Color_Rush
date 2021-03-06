package app.bizita.colorrush.ui.ui.privacy_policy;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import app.bizita.colorrush.R;


public class PrivacyPolicyFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        WebView webviewAboutUs = rootView.findViewById(R.id.webview_pp_us);
        ProgressBar progress_bar1 = rootView.findViewById(R.id.progress_bar);
        webviewAboutUs.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress_bar1.setVisibility(View.VISIBLE);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                progress_bar1.setVisibility(View.GONE);
                String webUrl = webviewAboutUs.getUrl();

            }

        });

//        webviewAboutUs.getSettings().setLoadWithOverviewMode(true);
//        webviewAboutUs.getSettings().setUseWideViewPort(true);



//        webviewAboutUs.setInitialScale(getScale());
//        webviewAboutUs.getSettings().setUseWideViewPort(true);
        webviewAboutUs.getSettings().setJavaScriptEnabled(true);
        webviewAboutUs.loadUrl("http://13.127.177.177/api/privacy");
//        webviewAboutUs.loadUrl("http://13.127.177.177/api/result_board");

        return rootView;
    }
//    private int getScale(){
//        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int width = display.getWidth();
//        Double val = new Double(width)/new Double(PIC_WIDTH);
//        val = val * 100d;
//        return val.intValue();
//    }
}