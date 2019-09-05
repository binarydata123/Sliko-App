package app.sliko.location;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import app.sliko.R;
import app.sliko.utills.M;
import app.sliko.web.ApiInterface;
import app.sliko.web.RetrofitClientInstance;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectAddressLocation extends AppCompatActivity {
    @BindView(R.id.searchedAddressRecyclerView)
    RecyclerView searchedAddressRecyclerView;
    @BindView(R.id.searchQuery)
    EditText searchQuery;
    @BindView(R.id.backImage)
    LinearLayout backImage;
    SearchAdapter searchAdapter;
    ArrayList<String> searchList;
    Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivty_location);
        ButterKnife.bind(SelectAddressLocation.this);
        dialog = M.showDialog(SelectAddressLocation.this, "", false);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    getSinglePitchDetail(searchQuery.getText().toString());
                    return true;
                }
                return false;
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
        dialog.show();
        ApiInterface service = RetrofitClientInstance.getRetrofitInstance().create(ApiInterface.class);
        Call<ResponseBody> call = service.ep_addressSuggestions(query);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                dialog.cancel();
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
                    Toast.makeText(SelectAddressLocation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(SelectAddressLocation.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}
