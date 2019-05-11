package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.EarningAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.PaymentHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vinod on 10/14/2016.
 */
public class ReferralEarning extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    TextView submit;
    String id;
    private static int GET_ALL = 70;
    private static int GET_PAID = 71;
    private static int GET_UNPAID = 72;
    private static int CASH_OUT = 73;
    int type = 0;
    ScrollView layInfo;
    RelativeLayout layList;
    TextView all, paid, unpaid, totalEarning, cmPaid, cmUnpaid, loyalty_dolar_earned;
    ListView list;
    SessionManager session;
    TextView noData;
    EarningAdapter adapter;
    TabLayout mTabLayout;
    TextView mCurrentBalance;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    int page = 1;
    int totalCount = 1;
    boolean loadingMore = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.referral_earning, null);
        init(view);
        //   updateUI(1);
        mTabLayout.getTabAt(0).select();
        return view;
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        HomeActivity.title.setText(getResources().getString(R.string.referral));
        layInfo = (ScrollView) view.findViewById(R.id.lay_info);
        layList = (RelativeLayout) view.findViewById(R.id.lay_list);
        totalEarning = (TextView) view.findViewById(R.id.total_earning);
        cmPaid = (TextView) view.findViewById(R.id.cm_paid);
        cmUnpaid = (TextView) view.findViewById(R.id.cm_unpaid);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        loyalty_dolar_earned = (TextView) view.findViewById(R.id.loyalty_dolar_earned);
        mCurrentBalance = (TextView) view.findViewById(R.id.currentbalance);
        noData = (TextView) view.findViewById(R.id.no_data);
        all = (TextView) view.findViewById(R.id.all);
        all.setOnClickListener(this);
        unpaid = (TextView) view.findViewById(R.id.unpaid);
        unpaid.setOnClickListener(this);
        paid = (TextView) view.findViewById(R.id.paid);
        paid.setOnClickListener(this);
        list = (ListView) view.findViewById(R.id.list);
        adapter = new EarningAdapter(getActivity(), data, "");
        list.setSmoothScrollbarEnabled(true);
        list.setAdapter(adapter);
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItemm, int visibleItemCountt,
                                 int totalItemCountt) {
                if (type == GET_UNPAID) {
                    firstVisibleItem = firstVisibleItemm;
                    visibleItemCount = visibleItemCountt;
                    totalItemCount = totalItemCountt;
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (type == GET_UNPAID) {
                    final int lastItem = firstVisibleItem + visibleItemCount;
                    if (firstVisibleItem > 0 && lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
                        if (!loadingMore) {
                            if (totalCount >= page) {
                                loadingMore = true;
                                getUnPaid();
                            }
                        }
                    }

                    //  new AsyncTask().execute();

                    //get next 10-20 items(your choice)items

                }
            }
        });
        /* list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String backStateName = this.getClass().getName();
                Bundle bundle = new Bundle();
                bundle.putString("delivery_id", data.get(i).getDeliveryId());
                bundle.putSerializable("incoming_type", "bank");
                Fragment fragment = new DeliveryInfo();
                fragment.setArguments(bundle);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        }); */
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.v("", String.valueOf(position));
                onTabTapped(tab.getPosition() + 1);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.v("", String.valueOf(position));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition() + 1);
            }
        });
    }


    public void onTabTapped(int position) {
        updateUI(position);
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d("data", result);
        handleResponse(result);
    }

    ArrayList<PaymentHistory> data = new ArrayList<PaymentHistory>();

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (type == GET_ALL) {
                    JSONObject obj = outJson.getJSONObject(Config.DATA);
                    totalEarning.setText(getResources().getString(R.string.referal_dolar_earned) + " : $" + obj.get(Keys.PAID_REFFERAL_AMOUNT).toString());
                    cmUnpaid.setText("$ " + obj.get(Keys.PAID_LOYALITY_AMOUNT).toString());
                    try {
                        String refeeralpaid = obj.get(Keys.AVL_REFFERAL_AMOUNT).toString();
                        String loyaltyPaid = obj.get(Keys.AVL_LOYALITY_AMOUNT).toString();
                        if (refeeralpaid.length() > 5) {
                            refeeralpaid = refeeralpaid.substring(0, 5);
                        }
                        if (loyaltyPaid.length() > 5) {
                            loyaltyPaid = loyaltyPaid.substring(0, 5);
                        }
                        cmPaid.setText("$ " + refeeralpaid);
                        loyalty_dolar_earned.setText("$" + loyaltyPaid);
                        Double val1 = Double.parseDouble(obj.get(Keys.AVL_REFFERAL_AMOUNT).toString());
                        Double val2 = Double.parseDouble(obj.get(Keys.AVL_LOYALITY_AMOUNT).toString());
                        Double add = val1 + val2;
                        mCurrentBalance.setText("$" + add);
                    } catch (Exception e) {
                        e.toString();
                    }

                    if (obj.get("cashout_button").toString().equals("1"))
                        submit.setVisibility(View.VISIBLE);
                    else submit.setVisibility(View.GONE);
                    //showMessage("Success!", getResources().getString(R.string.wallet_success));
                } else if (type == GET_PAID) {
                    JSONArray obj = outJson.optJSONArray(Config.DATA);

                    //   JSONObject obj = outJson.getJSONObject(Config.DATA);
                    data.clear();
                    // JSONArray referralArray = obj.getJSONArray("referral");
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject innerObj = obj.getJSONObject(i);
                        PaymentHistory model = new PaymentHistory();
                        model.setDate(innerObj.get("paid_on").toString());
                        model.setAmount(innerObj.get("amount").toString());
                        model.setType(innerObj.get("type").toString());
                        model.setDeliveryId(innerObj.get("job_id").toString());
                        model.setIspaid(true);
                        //model.setType(innerObj.get("earning_type_string").toString());
                        //  model.setDeliveryId(innerObj.get("delivery_id").toString());
                        data.add(model);
                    }
                    adapter.notifyDataSetChanged();
                    if (data.size() == 0) {
                        noData.setVisibility(View.VISIBLE);
                    } else noData.setVisibility(View.GONE);
                } else if (type == GET_UNPAID) {
                    JSONObject obj = outJson.getJSONObject(Config.DATA);
                    if (page == 1)
                        data.clear();
                    ++page;
                    loadingMore = false;
                    JSONArray referralArray = obj.getJSONArray("referral");
                    for (int i = 0; i < referralArray.length(); i++) {
                        JSONObject innerObj = referralArray.getJSONObject(i);
                        PaymentHistory model = new PaymentHistory();
                        model.setDate(innerObj.get("created_at").toString());
                        model.setAmount(innerObj.get("amount").toString());
                        model.setType(innerObj.get("earning_type_string").toString());
                        model.setDeliveryId(innerObj.get("job_id").toString());
                        model.setIspaid(false);
                        data.add(model);
                    }
                    adapter.notifyDataSetChanged();
                    try {
                        if (obj.has("_meta")) {
                            JSONObject metaCount = obj.getJSONObject("_meta");
                            totalCount = Integer.parseInt(metaCount.optString("pageCount"));
                            Log.v("totalcount", String.valueOf(totalCount));

                        }
                    } catch (Exception e) {
                        e.toString();
                    }
                    if (data.size() == 0) {
                        noData.setVisibility(View.VISIBLE);
                    } else noData.setVisibility(View.GONE);
                } else if (type == CASH_OUT) {
                    messageCheckOut(getActivity(), "Success!", outJson.getString(Config.MESSAGE));
                    //   UpdateFragment();
                }
            } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                showMessage("Error!", outJson.getString(Config.MESSAGE));

            } else if (outJson.getInt(Config.STATUS) == APIStatus.INVALID_CARD) {
                try {
                    AlertManager.BankmessageDialog((HomeActivity) getActivity(), "Alert!", outJson.getString(Config.MESSAGE), this.getClass().getName(), "3");
                } catch (Exception e) {
                    e.toString();
                }
            } else {
                showMessage("Error!", getResources().getString(R.string.no_response));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void UpdateFragment() {
        try {
            FragmentTransaction ftr = getActivity().getFragmentManager().beginTransaction();
            ftr.detach(ReferralEarning.this).attach(ReferralEarning.this).commit();
        } catch (Exception e) {
            e.toString();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                cashOut();
                break;
            case R.id.all:
                updateUI(1);
                break;
            case R.id.paid:
                updateUI(2);
                break;
            case R.id.unpaid:
                updateUI(3);
                break;
        }
    }

    String TITLE = "Alert!";

    public void messageCheckOut(Context ctx, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                UpdateFragment();
            }
        });
        Dialog d = builder.create();
        d.show();
    }

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(getActivity(), title, message);
    }

    private void updateUI(int type) {
        if (type == 1) {
            all.setBackgroundResource(R.drawable.border_back);
            all.setTextColor(Color.WHITE);
            paid.setBackgroundResource(R.drawable.border_black);
            paid.setTextColor(Color.BLACK);
            unpaid.setBackgroundResource(R.drawable.border_black);
            unpaid.setTextColor(Color.BLACK);
            layInfo.setVisibility(View.VISIBLE);
            layList.setVisibility(View.GONE);
        } else if (type == 2) {
            all.setBackgroundResource(R.drawable.border_black);
            all.setTextColor(Color.BLACK);
            paid.setBackgroundResource(R.drawable.border_back);
            paid.setTextColor(Color.WHITE);
            unpaid.setBackgroundResource(R.drawable.border_black);
            unpaid.setTextColor(Color.BLACK);
            layInfo.setVisibility(View.GONE);
            layList.setVisibility(View.VISIBLE);
        } else if (type == 3) {
            all.setBackgroundResource(R.drawable.border_black);
            all.setTextColor(Color.BLACK);
            paid.setBackgroundResource(R.drawable.border_black);
            paid.setTextColor(Color.BLACK);
            unpaid.setBackgroundResource(R.drawable.border_back);
            unpaid.setTextColor(Color.WHITE);
            layInfo.setVisibility(View.GONE);
            layList.setVisibility(View.VISIBLE);
        }
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.filter.setVisibility(View.GONE);
        getList(type);
    }

    private void getList(int type) {
        data.clear();
        String url = Config.SERVER_URL;
        switch (type) {
            case 1:
                this.type = GET_ALL;
                url = url + Config.REFERRAL_ALL;
                break;
            case 2:
                this.type = GET_PAID;
                url = url + Config.DRIVERTRANSACTIONS;
                submit.setVisibility(View.GONE);
                break;
            case 3:
                this.type = GET_UNPAID;
                url = url + Config.REFERRAL_UNPAID;
                submit.setVisibility(View.GONE);
                page = 1;
                getUnPaid();
                break;
        }
        if (type == 1 || type == 2) {
            if (Internet.hasInternet(getActivity())) {
                RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
                mobileAPI.execute(url, session.getToken());
            } else
                AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
        }
    }

    public void getUnPaid() {
        String url = Config.SERVER_URL + Config.REFERRAL_UNPAID + "?page=" + page;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void cashOut() {
        String url = Config.SERVER_URL;
        this.type = CASH_OUT;
        url = url + Config.CASH_OUT;

        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

}