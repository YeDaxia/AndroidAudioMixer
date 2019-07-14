package io.github.yedaxia.musicnote;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.github.yedaxia.musicnote.activity.BaseActivity;
import io.github.yedaxia.musicnote.activity.MetronomeActivity;
import io.github.yedaxia.musicnote.activity.WebViewActivity;
import io.github.yedaxia.musicnote.activity.WorkspaceActivity;
import io.github.yedaxia.musicnote.app.AppContext;
import io.github.yedaxia.musicnote.app.util.BundleKeys;
import io.github.yedaxia.musicnote.app.util.DeviceUtils;
import io.github.yedaxia.musicnote.app.util.ToastUtils;
import io.github.yedaxia.musicnote.data.api.Urls;
import io.github.yedaxia.musicnote.data.api.AppApi;
import io.github.yedaxia.musicnote.data.entity.Project;
import io.github.yedaxia.musicnote.data.entity.ProjectDao;
import io.github.yedaxia.musicnote.data.entity.Track;
import io.github.yedaxia.musicnote.data.entity.TrackDao;
import io.github.yedaxia.musicnote.data.sp.SpApp;
import io.github.yedaxia.musicnote.ui.adapter.AbsRVAdapter;
import io.github.yedaxia.musicnote.ui.adapter.IRViewHolder;
import io.github.yedaxia.musicnote.ui.dialog.MAlertDialog;
import io.github.yedaxia.musicnote.ui.dialog.MDialogBuilder;
import io.github.yedaxia.musicnote.ui.dialog.MEditorDialog;
import io.github.yedaxia.musicnote.ui.dialog.MListDialog;
import io.github.yedaxia.musicnote.util.FileUtils;
import io.github.yedaxia.musicnote.util.ListUtils;
import io.github.yedaxia.musicnote.util.StringUtils;

public class MainActivity extends BaseActivity {

    private static final int REQ_PAY_INTENT = 0x11;

    @BindView(R.id.rv_project_list)
    RecyclerView rvProjectList;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.nv_menu_left)
    NavigationView navView;

    @BindView(R.id.layout_empty)
    LinearLayout emptyLayout;

    private ProjectDao projectDao;
    private List<Project> projectListData;
    private ProjectListAdapter projectAdapter;
    private TrackDao trackDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initActionBar();
        projectDao = AppContext.getDaoSession().getProjectDao();
        trackDao = AppContext.getDaoSession().getTrackDao();
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_help:
                        onNavHelpClick();
                        break;
                    case R.id.nav_feedback:
                        onNavFeedbackClick();
                        break;
                    case R.id.nav_about:
                        onNavAboutClick();
                        break;
                    case R.id.nav_metronome:
                        onNavMetronomeClick();
                        break;
                    case R.id.nav_search_tab:
                        onSearchTabClick();
                        break;
                }
                return true;
            }
        });
        loadProjects();
        checkAppActivate();
    }

    private void onSearchTabClick() {
        WebViewActivity.launch(this, getString(R.string.about), Urls.getSearchTabUrl());
    }

    @OnClick(R.id.btn_help)
    void onHelpClick(View v) {
        onNavHelpClick();
    }

    private void onNavMetronomeClick() {
        MetronomeActivity.launch(this);
    }

    private void onNavAboutClick() {
        WebViewActivity.launch(this, getString(R.string.about), Urls.getAboutUrl());
    }

    private void onNavFeedbackClick() {
        WebViewActivity.launch(this, getString(R.string.feedback), Urls.getFeedbackUrl());
    }

    private void onNavHelpClick() {
        WebViewActivity.launch(this, getString(R.string.help), Urls.getHelpUrl());
    }

    private void initActionBar() {
        // 使用ActionBarDrawerToggle，配合DrawerLayout和ActionBar,以实现推荐的抽屉功能。
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                getToolbar(), R.string.open, R.string.close);
        mDrawerToggle.syncState();
        drawerLayout.addDrawerListener(mDrawerToggle);
    }

    @OnClick(R.id.btn_add_project)
    void onAddProjectClick(View v) {

        if (ListUtils.isNotEmpty(projectListData) &&
                projectListData.size() == 3 && !SpApp.sp().isAppActivate()) {
            showActivateDialog();
            return;
        }

        new MEditorDialog.Builder(this)
                .title(R.string.project_name)
                .positiveText(R.string.confirm)
                .positiveClickListener(new MDialogBuilder.OnButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        String projectName = ((MEditorDialog) dialog).getText().toString().trim();
                        if (StringUtils.isEmpty(projectName)) {
                            ToastUtils.showErrorToast(MainActivity.this, R.string.error_empty_project_name);
                            return;
                        }
                        addNewProject(projectName);
                    }
                })
                .build()
                .show();
    }

    private void loadProjects() {
        new Thread() {
            @Override
            public void run() {
                projectListData = projectDao.loadAll();
                if (projectListData == null) {
                    projectListData = new ArrayList<>();
                }
                sendEmptyUiMessage(R.integer.LOAD_DATA_SUCCESS);
            }
        }.start();
    }

    @Override
    protected void handleUiMessage(Message msg) {
        super.handleUiMessage(msg);
        if (msg.what == R.integer.LOAD_DATA_SUCCESS) {

            if (ListUtils.isEmpty(projectListData)) {
                emptyLayout.setVisibility(View.VISIBLE);
            } else {
                emptyLayout.setVisibility(View.GONE);
            }

            if (projectAdapter == null) {
                projectAdapter = new ProjectListAdapter(this, projectListData);
                rvProjectList.setLayoutManager(new LinearLayoutManager(this));
                rvProjectList.setAdapter(projectAdapter);
            }
        }
    }

    private void showActivateDialog() {
        new MAlertDialog.Builder(this)
                .title(R.string.tips)
                .message(R.string.app_activate_tip)
                .negativeText(R.string.cancel)
                .positiveText(R.string.buy)
                .positiveClickListener(new MDialogBuilder.OnButtonClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        Intent payIntent = new Intent(MainActivity.this, WebViewActivity.class);
                        payIntent.putExtra(BundleKeys.WEB_URL, Urls.getBuyAppUrl(DeviceUtils.getDeviceId()));
                        payIntent.putExtra(BundleKeys.TITLE, getString(R.string.activate_app));
                        startActivityForResult(payIntent, REQ_PAY_INTENT);
                    }
                })
                .build()
                .show();
    }

    private void addNewProject(String projectName) {
        Project project = new Project();
        project.setName(projectName);
        project.setSpeed((short) 120);
        project.setBeat("4/4");
        project.setTune("C");
        project.setCreateTime(new Date());
        long projectId = projectDao.insert(project);
        project.setId(projectId);

        projectListData.add(project);
        projectAdapter.notifyDataSetChanged();
        emptyLayout.setVisibility(View.GONE);

        WorkspaceActivity.launch(this, project);
    }

    private void deleteProject(Project project) {
        List<Track> trackList = trackDao.queryBuilder()
                .where(TrackDao.Properties.ProjectId.eq(project.getId()))
                .list();
        if (ListUtils.isNotEmpty(trackList)) {
            for (Track track : trackList) {
                trackDao.delete(track);
                FileUtils.deleteFile(new File(AppContext.getAudioTempPath(), track.getFileName()));
            }
        }
        projectDao.delete(project);
        projectListData.remove(project);
        projectAdapter.notifyDataSetChanged();
        emptyLayout.setVisibility(View.GONE);
    }

    private void updateProject(Project project) {
        projectDao.update(project);
    }

    private void checkAppActivate() {
        new CheckActivateThread().start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_PAY_INTENT){
            checkAppActivate();
        }
    }

    private class ProjectListAdapter extends AbsRVAdapter<Project> {

        public ProjectListAdapter(Context context, List<Project> data) {
            super(context, data);
        }

        @Override
        protected int getItemViewType(int position, Project model) {
            return 0;
        }

        @Override
        protected IRViewHolder createViewHolder(int viewType) {
            return new VHProject();
        }
    }

    class VHProject implements IRViewHolder<Project> {

        @BindView(R.id.tv_project_name)
        TextView tvProjectName;

        @BindView(R.id.tv_tune)
        TextView tvTune;

        Project project;

        @Override
        public View getItemView(LayoutInflater inflater, ViewGroup parentView) {
            return inflater.inflate(R.layout.item_project, parentView, false);
        }

        @Override
        public void findViews(View viewContainer, int viewType) {
            ButterKnife.bind(this, viewContainer);
        }

        @Override
        public void bindViewData(int position, Project model, int viewType) {
            tvProjectName.setText(model.getName());
            tvTune.setText(model.getTune());
            this.project = model;
        }

        @OnClick(R.id.list_item)
        void onItemCLick(View v) {
            WorkspaceActivity.launch(v.getContext(), project);
        }

        @OnLongClick(R.id.list_item)
        boolean onItemLongClick(View v) {
            new MListDialog.Builder(v.getContext())
                    .items(R.array.project_menu_array)
                    .onItemClick(new MListDialog.OnItemClickListener() {
                        @Override
                        public void onItemClick(MListDialog dialog, int position, CharSequence text) {
                            if (position == 0) {
                                onTrackRenameClick(dialog.getContext());
                            } else {
                                onTrackDeleteClick(dialog.getContext());
                            }
                        }
                    })
                    .build()
                    .show();
            return true;
        }

        private void onTrackDeleteClick(Context context) {
            new MAlertDialog.Builder(context)
                    .title(R.string.warning)
                    .message(R.string.delete_project_tip)
                    .positiveText(R.string.delete)
                    .negativeText(R.string.cancel)
                    .positiveClickListener(new MDialogBuilder.OnButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            deleteProject(project);
                        }
                    })
                    .build()
                    .show();
        }

        private void onTrackRenameClick(final Context cxt) {
            new MEditorDialog.Builder(cxt)
                    .title(R.string.track_name_setting)
                    .positiveText(R.string.confirm)
                    .inputText(project.getName())
                    .positiveClickListener(new MDialogBuilder.OnButtonClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            String projectName = ((MEditorDialog) dialog).getText().toString().trim();
                            if (StringUtils.isEmpty(projectName)) {
                                ToastUtils.showErrorToast(cxt, R.string.error_empty_project_name);
                                return;
                            }
                            tvProjectName.setText(projectName);
                            project.setName(projectName);
                            updateProject(project);
                        }
                    })
                    .build()
                    .show();
        }
    }

    static class CheckActivateThread extends Thread {

        @Override
        public void run() {
            try {
                AppApi appApi = new AppApi();
                boolean activate = appApi.checkActivate(DeviceUtils.getDeviceId());
                SpApp.sp().saveAppActivate(activate);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
