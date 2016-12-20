package com.example.renhao.wevolunteer.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.core.AppActionImpl;
import com.example.core.local.LocalDate;
import com.example.model.ActionCallbackListener;
import com.example.model.PagedListEntityDto;
import com.example.model.activity.ActivityListDto;
import com.example.model.activity.ActivityQueryOptionDto;
import com.example.model.content.ContentListDto;
import com.example.model.content.ContentQueryOptionDto;
import com.example.model.items.HomePageGridItem;
import com.example.model.items.HomePageListItem;
import com.example.renhao.wevolunteer.OrganizationActivity;
import com.example.renhao.wevolunteer.ProjectActivity;
import com.example.renhao.wevolunteer.ProjectDetailActivity;
import com.example.renhao.wevolunteer.R;
import com.example.renhao.wevolunteer.activity.RegisterActivity;
import com.example.renhao.wevolunteer.activity.RegisterProBonoActivity;
import com.example.renhao.wevolunteer.adapter.HomePageAdapter;
import com.example.renhao.wevolunteer.adapter.HomePageNoScrollGridAdapter;
import com.example.renhao.wevolunteer.base.BaseActivity;
import com.example.renhao.wevolunteer.base.BaseFragment;
import com.example.renhao.wevolunteer.event.FragmentResultEvent;
import com.example.renhao.wevolunteer.utils.Util;
import com.example.renhao.wevolunteer.view.NoScrollGridView;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnLoadImageListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 项目名称：WeVolunteer
 * 类描述：主页
 * 创建人：renhao
 * 创建时间：2016/8/5 16:38
 * 修改备注：
 */
public class HomePageFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private static final String TAG = "HomePageFragment";

    public static final int PROJECT = 0;
    public static final int ORGANIZATION = 1;

    private PullToRefreshListView mPtrListviewHomapageList;
    private Banner banner;
    private View imageBanner;
    private View imageSliderView;
    private NoScrollGridView gridView;
    private HomePageAdapter adapter;
    public BaseActivity mBaseActivity;
    private boolean SliderData_connect_again = true;
    private boolean Data_connect_again = true;

    private List<HomePageListItem> list = null;
    private List<ActivityListDto> dates = new ArrayList<>();


    private int loadFinish = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_homepage, container, false);

        mBaseActivity = (BaseActivity) getActivity();

        getDate();
        initImageSliderView();

        initGridButton();

        initPullToRefreshListView(view);
        return view;
    }

    /**
     * 初始化下拉刷新控件
     *
     * @param view
     */
    private void initPullToRefreshListView(View view) {
        mPtrListviewHomapageList = (PullToRefreshListView) view.findViewById(R.id.ptrListview_homapage_list);

        this.mPtrListviewHomapageList.getRefreshableView().addHeaderView(this.imageBanner);
        this.mPtrListviewHomapageList.getRefreshableView().addHeaderView(gridView);//添加imageslider


        mPtrListviewHomapageList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getDate();
                getSliderDate();
            }
        });
        mPtrListviewHomapageList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);//设置头部下拉刷新
        //设置刷新时显示的文本
        ILoadingLayout startLayout = mPtrListviewHomapageList.getLoadingLayoutProxy(true, false);//开始头部的layout
        startLayout.setPullLabel("下拉刷新....");
        startLayout.setRefreshingLabel("正在玩命加载....");
        startLayout.setReleaseLabel("放开刷新");

        list = new ArrayList<>();
        adapter = new HomePageAdapter(getActivity(), list);
        mPtrListviewHomapageList.setAdapter(adapter);
        mPtrListviewHomapageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.v(TAG, "position " + position);
                Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                intent.putExtra("id", dates.get(position - 3).getId());
                intent.putExtra("type", dates.get(position - 3).getType());
                startActivity(intent);
            }
        });
    }

    private void getDate() {
        mBaseActivity.showNormalDialog("数据加载中…");
        ActivityQueryOptionDto query = new ActivityQueryOptionDto();
        LinkedHashMap<String, String> sorts_map = new LinkedHashMap<>();
        sorts_map.put("StickTime", "desc");
        sorts_map.put("Stick", "desc");
        sorts_map.put("StartTime", "desc");
        query.setPageIndex(1);
        query.setStick(1);
        query.setPageSize(6);
        query.setLanguageType("");
        query.setSorts(sorts_map);
        AppActionImpl.getInstance(getActivity()).activityQuery(query, new ActionCallbackListener<PagedListEntityDto<ActivityListDto>>() {
            @Override
            public void onSuccess(PagedListEntityDto<ActivityListDto> data) {
                list = new ArrayList<>();
                dates = data.getRows();
                for (int i = 0; i < dates.size(); i++) {
                    ActivityListDto dto = dates.get(i);
                    HomePageListItem item = new HomePageListItem();
                    item.setType(dto.getType());
                    item.setState(dto.getOperationState());
                    item.setTitle(dto.getActivityName());
                    item.setNum(dto.getRecruited());
                    item.setMaxNum(dto.getRecruitNumber());
                    item.setTime(dto.getLengthTime());
                    item.setImg(dto.getAppLstUrl());
                    list.add(item);
                }
                adapter.setDate(list);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                /*if (Data_connect_again) {
                    Data_connect_again = false;
                    getDate();
                }*/
            }
        });
    }

    /**
     * 初始化按钮，并为按钮绑定监听
     */
    private void initGridButton() {
        final Intent intent = new Intent();
        gridView = new NoScrollGridView(getActivity());
        //gridView.setBackgroundResource(R.drawable.border_shallowblack);
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final boolean isLogin = LocalDate.getInstance(getActivity()).getLocalDate("isLogin", false);
                String volunteerId = LocalDate.getInstance(getActivity()).getLocalDate("volunteerId", "");
                switch (position) {
                    case 0:
                        intent.setClass(getActivity(), RegisterActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(getActivity(), RegisterProBonoActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(getActivity(), OrganizationActivity.class);
                        startActivityForResult(intent, ORGANIZATION);
                        break;
                    case 3:
                        intent.setClass(getActivity(), ProjectActivity.class);
                        intent.putExtra("page", ProjectActivity.JOBS);
                        startActivityForResult(intent, PROJECT);
                        break;
                    case 4:
                        intent.setClass(getActivity(), ProjectActivity.class);
                        intent.putExtra("page", ProjectActivity.ACTIVITY);
                        startActivityForResult(intent, PROJECT);
                        break;
                    case 5:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("此功能正在开发中");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.create().show();
                        break;
                }
            }
        });
        ArrayList<HomePageGridItem> items = new ArrayList<>();
        items.add(new HomePageGridItem(R.drawable.regist, "注册", "志愿者", R.color.colorCyan));
        items.add(new HomePageGridItem(R.drawable.professionalregist, "注册", "专业志愿者", R.color.colorCyan));
        items.add(new HomePageGridItem(R.drawable.organization, "组织", null, R.color.colorCyan));
        items.add(new HomePageGridItem(R.drawable.jobs, "岗位", null, R.color.colorBlue));
        items.add(new HomePageGridItem(R.drawable.activity, "活动", null, R.color.colorGreen));
        items.add(new HomePageGridItem(R.drawable.shop, "积分商城", null, R.color.colorOrange));
        gridView.setAdapter(new HomePageNoScrollGridAdapter(getActivity(), items));
    }

//    private void showToast(String msg) {
//
//        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
//    }

    /**
     * 初始化Banner图片滑动器
     */
    private void initImageSliderView() {
        this.imageBanner = LayoutInflater.from(getActivity()).inflate(R.layout.view_imagebanner, null);
        banner = (Banner) imageBanner.findViewById(R.id.homepage_banner);
        getSliderDate();
    }

    /**
     * 获取首页新闻作为滚动栏
     */
    private void getSliderDate() {
        ContentQueryOptionDto queryOptionDto = new ContentQueryOptionDto();
        queryOptionDto.setPermission(true);
        queryOptionDto.setCategoryId("434d9fae-f27d-4739-b99a-ceb21f79171a");
        queryOptionDto.setPageSize(4);
        AppActionImpl.getInstance(getActivity()).contentQuery(queryOptionDto,
                new ActionCallbackListener<PagedListEntityDto<ContentListDto>>() {
                    @Override
                    public void onSuccess(PagedListEntityDto<ContentListDto> data) {
                        mBaseActivity.dissMissNormalDialog();
                        if (data == null) {
                            mPtrListviewHomapageList.onRefreshComplete();
                            return;
                        }
                        if (data.getRows() == null) {
                            mPtrListviewHomapageList.onRefreshComplete();
                            return;
                        }
                        final String[] banner_img = new String[data.getRows().size()];
                        String[] banner_title = new String[data.getRows().size()];
                        for (int i = 0; i < data.getRows().size(); i++) {
                            banner_img[i] = Util.getRealUrl(data.getRows().get(i).getSmallImgViewPc());
                            banner_title[i] = data.getRows().get(i).getContentName();
                        }
                        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
                        banner.setIndicatorGravity(BannerConfig.CENTER);
                        banner.setDelayTime(3000);
                        banner.setBannerTitle(banner_title);
                        //可以选择设置图片网址，或者资源文件，默认加载框架Glide
                        banner.setImages(banner_img, new OnLoadImageListener() {
                            @Override
                            public void OnLoadImage(ImageView view, Object url) {
                                try {
                                    Glide.with(getActivity())
                                            .load(url)
                                            .error(R.drawable.img_unload)
                                            .placeholder(R.drawable.img_unload)
                                            .fitCenter()
                                            .into(view);
                                }catch (Exception e){

                                }

                            }
                        });
                        mPtrListviewHomapageList.onRefreshComplete();
                    }

                    @Override
                    public void onFailure(String errorEvent, String message) {
                        /*if (SliderData_connect_again) {
                            SliderData_connect_again = false;
                            getSliderDate();
                        } else*/
                        mBaseActivity.dissMissNormalDialog();
                        showToast("获取数据失败，请检查网络后重试");
                        mPtrListviewHomapageList.onRefreshComplete();
                    }
                });
    }


    //图片滑动器的监听

    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        super.onStop();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Logger.v(TAG, "page  position  " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Logger.v(TAG, "image click  " + slider.getBundle().getString("extra"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //使用EventBus向IndexActivity传值
        EventBus.getDefault().post(new FragmentResultEvent(requestCode, resultCode, data));
    }

    @Override
    public void onResume() {
        super.onResume();
        //自动刷新获取首页的内容
       /* Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //自动刷新获取首页的内容
                try {
                    getDate();
                    getSliderDate();
                } catch (Exception e) {
                }
            }
        }, 100);*/
    }
}
