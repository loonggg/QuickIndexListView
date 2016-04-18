package net.loonggg.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class MainActivity extends Activity {
	private HashMap<String, Integer> selector;// 存放含有索引字母的位置
	private LinearLayout layoutIndex;
	private ListView listView;
	private TextView tv_show;
	private ListViewAdapter adapter;
	private String[] indexStr = { "#", "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z" };
	private List<Person> persons = null;
	private List<Person> newPersons = new ArrayList<Person>();
	private int height;// 字体高度
	private boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		layoutIndex = (LinearLayout) this.findViewById(R.id.layout);
		layoutIndex.setBackgroundColor(Color.parseColor("#00ffffff"));
		listView = (ListView) findViewById(R.id.listView);
		tv_show = (TextView) findViewById(R.id.tv);
		tv_show.setVisibility(View.GONE);
		setData();
		String[] allNames = sortIndex(persons);
		sortList(allNames);

		selector = new HashMap<String, Integer>();
		for (int j = 0; j < indexStr.length; j++) {// 循环字母表，找出newPersons中对应字母的位置
			for (int i = 0; i < newPersons.size(); i++) {
				if (newPersons.get(i).getName().equals(indexStr[j])) {
					selector.put(indexStr[j], i);
				}
			}

		}
		adapter = new ListViewAdapter(this, newPersons);
		listView.setAdapter(adapter);
	}

	/**
	 * 重新排序获得一个新的List集合
	 * 
	 * @param allNames
	 */
	private void sortList(String[] allNames) {
		for (int i = 0; i < allNames.length; i++) {
			if (allNames[i].length() != 1) {
				for (int j = 0; j < persons.size(); j++) {
					if (allNames[i].equals(persons.get(j).getPinYinName())) {
						Person p = new Person(persons.get(j).getName(), persons
								.get(j).getPinYinName());
						newPersons.add(p);
					}
				}
			} else {
				newPersons.add(new Person(allNames[i]));
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// 在oncreate里面执行下面的代码没反应，因为oncreate里面得到的getHeight=0
		if (!flag) {// 在这里Activity失去焦点或者被覆盖时，每次进入都会再次执行，是的字母索引总是变化，所以加上标记，使其只执行一次
			height = layoutIndex.getMeasuredHeight() / indexStr.length;
			getIndexView();
			flag = true;
		}
	}

	/**
	 * 获取排序后的新数据
	 * 
	 * @param persons
	 * @return
	 */
	public String[] sortIndex(List<Person> persons) {
		TreeSet<String> set = new TreeSet<String>();
		// 获取初始化数据源中的首字母，添加到set中
		for (Person person : persons) {
			set.add(StringHelper.getPinYinHeadChar(person.getName()).substring(
					0, 1));
		}
		// 新数组的长度为原数据加上set的大小
		String[] names = new String[persons.size() + set.size()];
		int i = 0;
		for (String string : set) {
			names[i] = string;
			i++;
		}
		String[] pinYinNames = new String[persons.size()];
		for (int j = 0; j < persons.size(); j++) {
			persons.get(j).setPinYinName(
					StringHelper
							.getPingYin(persons.get(j).getName().toString()));
			pinYinNames[j] = StringHelper.getPingYin(persons.get(j).getName()
					.toString());
		}
		// 将原数据拷贝到新数据中
		System.arraycopy(pinYinNames, 0, names, set.size(), pinYinNames.length);
		// 自动按照首字母排序
		Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
		return names;
	}

	/**
	 * 绘制索引列表
	 */
	public void getIndexView() {
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.WRAP_CONTENT, height);
		for (int i = 0; i < indexStr.length; i++) {
			final TextView tv = new TextView(this);
			tv.setLayoutParams(params);
			tv.setText(indexStr[i]);
			tv.setPadding(10, 0, 10, 0);
			layoutIndex.addView(tv);
			layoutIndex.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event)

				{
					float y = event.getY();
					int index = (int) (y / height);
					if (index > -1 && index < indexStr.length) {// 防止越界
						String key = indexStr[index];
						if (selector.containsKey(key)) {
							int pos = selector.get(key);
							if (listView.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有。
								listView.setSelectionFromTop(
										pos + listView.getHeaderViewsCount(), 0);
							} else {
								listView.setSelectionFromTop(pos, 0);// 滑动到第一项
							}
							tv_show.setVisibility(View.VISIBLE);
							tv_show.setText(indexStr[index]);
						}
					}
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						layoutIndex.setBackgroundColor(Color
								.parseColor("#606060"));
						break;

					case MotionEvent.ACTION_MOVE:

						break;
					case MotionEvent.ACTION_UP:
						layoutIndex.setBackgroundColor(Color
								.parseColor("#00ffffff"));
						tv_show.setVisibility(View.GONE);
						break;
					}
					return true;
				}
			});
		}
	}

	/**
	 * 设置模拟数据
	 */
	private void setData() {
		persons = new ArrayList<Person>();
		Person p1 = new Person("耿琦");
		persons.add(p1);
		Person p2 = new Person("王宝强");
		persons.add(p2);
		Person p3 = new Person("柳岩");
		persons.add(p3);
		Person p4 = new Person("文章");
		persons.add(p4);
		Person p5 = new Person("马伊琍");
		persons.add(p5);
		Person p6 = new Person("李晨");
		persons.add(p6);
		Person p7 = new Person("张馨予");
		persons.add(p7);
		Person p8 = new Person("韩红");
		persons.add(p8);
		Person p9 = new Person("韩寒");
		persons.add(p9);
		Person p10 = new Person("丹丹");
		persons.add(p10);
		Person p11 = new Person("丹凤眼");
		persons.add(p11);
		Person p12 = new Person("哈哈");
		persons.add(p12);
		Person p13 = new Person("萌萌");
		persons.add(p13);
		Person p14 = new Person("蒙混");
		persons.add(p14);
		Person p15 = new Person("烟花");
		persons.add(p15);
		Person p16 = new Person("眼黑");
		persons.add(p16);
		Person p17 = new Person("许三多");
		persons.add(p17);
		Person p18 = new Person("程咬金");
		persons.add(p18);
		Person p19 = new Person("程杰");
		persons.add(p19);
		Person p20 = new Person("死了吧");
		persons.add(p20);
		Person p21 = new Person("阿莱");
		persons.add(p21);

	}

}
