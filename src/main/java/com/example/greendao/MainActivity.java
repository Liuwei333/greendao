package com.example.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.greendao.greendao.DaoMaster;
import com.example.greendao.greendao.DaoSession;
import com.example.greendao.greendao.User;
import com.example.greendao.greendao.UserDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.pass)
    EditText pass;
    @BindView(R.id.rcy)
    RecyclerView rcy;
    @BindView(R.id.but)
    Button but;
    @BindView(R.id.select)
    Button select;
    @BindView(R.id.delete)
    Button delete;
    @BindView(R.id.update)
    Button update;
    @BindView(R.id.edit)
    EditText edit;

    @BindView(R.id.all)
    Button all;
    private UserDao userDao;
    private User user;
    private String name1;
    private String pass1;
    private MyAdapter myAdapter;
    private List<User> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        //实现创建
        SQLiteDatabase database = new DaoMaster.DevOpenHelper(this, "stui.db").getWritableDatabase();

        //连接
        DaoMaster master = new DaoMaster(database);

        //操作类
        DaoSession daoSession = master.newSession();
        userDao = daoSession.getUserDao();


        rcy.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));

    }

    @OnClick({R.id.but, R.id.select, R.id.delete, R.id.update, R.id.all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.but:

                insert();
                break;
            case R.id.select:
                cha();
                break;
            case R.id.delete:
                shachu();
                break;
            case R.id.update:
                upda();
                break;
            case R.id.all:
                all();
                break;

        }
    }

    //全部删除
    private void all() {

        userDao.deleteAll();

        cha();
    }


    //修改
    private void upda() {

        String id = edit.getText().toString().trim();

        long ids = Long.parseLong(id);


        List<User> users = userDao.loadAll();

        for(int i=0;i<users.size();i++){
            Long id1 = users.get(i).getId();
            Log.d("aa", "select: "+id1);

            if(ids==id1){
                String s = name.getText().toString();
                String age = pass.getText().toString();
                User user=new User(ids,s,age);

                // user.setAge(age);
                // user.setName(s);

                userDao.update(user);

                Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this,"空的",Toast.LENGTH_SHORT).show();
            }

        }
        // User user = userDao.queryBuilder().where(UserDao.Properties.Name.eq(name)).build().unique();

        cha();

    }

    //删除
    private void shachu() {
        //获取id值
        String id = edit.getText().toString().trim();

        userDao.queryBuilder().where(UserDao.Properties.Id.eq(id)).buildDelete().executeDeleteWithoutDetachingEntities();
        Toast.makeText(this, "删除成功~", Toast.LENGTH_SHORT).show();
        cha();
    }

    //查询
    private void cha() {
        list = userDao.loadAll();

        myAdapter = new MyAdapter(MainActivity.this, list);
        rcy.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }

    //添加
    private void insert() {
        pass1 = pass.getText().toString();
        name1 = name.getText().toString();

        if (name1.isEmpty() && pass1.isEmpty()) {
            Toast.makeText(MainActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
        } else {
            User student = new User(null, pass1, name1);
            long insert = userDao.insert(student);
            Log.d("aa", "insert: " + insert);

            cha();
        }

    }

    //适配器
    class MyAdapter extends RecyclerView.Adapter {
        Context context;

        List<User> list;

        public MyAdapter(Context context, List<User> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View inflate = View.inflate(context, R.layout.buju, null);
            MyHolder myHolder = new MyHolder(inflate);
            return myHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            MyHolder holder1 = (MyHolder) holder;

            holder1.name.setText(list.get(position).getName() + "---");
            holder1.pass.setText(list.get(position).getAge() + "---");
            holder1.ide.setText(list.get(position).getId() + "");


        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView pass;
        private final TextView ide;

        public MyHolder(View view) {
            super(view);

            name = view.findViewById(R.id.name);

            pass = view.findViewById(R.id.pass);

            ide = view.findViewById(R.id.id);
        }
    }
}
