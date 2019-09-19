package app.sliko.location;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.UI.SsMediumTextView;
import app.sliko.UI.SsRegularEditText;
import app.sliko.UI.SsRegularTextView;
import app.sliko.utills.M;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindLocationActivity extends AppCompatActivity {
    @BindView(R.id.searchedAddressRecyclerView)
    RecyclerView searchedAddressRecyclerView;
    @BindView(R.id.searchQuery)
    SsRegularEditText searchQuery;
    @BindView(R.id.progress)
    ProgressBar progress;
    @BindView(R.id.toolbarTitle)
    SsMediumTextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;   @BindView(R.id.searchResultLayout)
    CardView searchResultLayout;
    SearchAdapter searchAdapter;
    ArrayList<String> searchList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivty_location);
        ButterKnife.bind(FindLocationActivity.this);
        toolbar.setNavigationIcon(R.drawable.back_arrow_white);
        toolbarTitle.setText(getString(R.string.findLocation));
        searchResultLayout.setVisibility(View.GONE);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        searchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    searchResultLayout.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.VISIBLE);
                    getSinglePitchDetail(searchQuery.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setAdapter() {
        searchAdapter = new SearchAdapter(this, searchList);
        searchedAddressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchedAddressRecyclerView.setAdapter(searchAdapter);
    }

    private void getSinglePitchDetail(String query) {
        searchList = new ArrayList<>();
        searchList.clear();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_addressSuggestions(query);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                progress.setVisibility(View.GONE);
                try {
                    String sResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(sResponse);
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    if (dataArray.length() > 0) {
                        for (int k = 0; k < dataArray.length(); k++) {
                            String getAddressString = dataArray.getString(k);
                            searchList.add(getAddressString);
                        }
                        setAdapter();
                        searchAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Toast.makeText(FindLocationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
               progress.setVisibility(View.GONE);
                Toast.makeText(FindLocationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}
