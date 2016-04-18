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
	private HashMap<String, Integer> selector;// ��ź���������ĸ��λ��
	private LinearLayout layoutIndex;
	private ListView listView;
	private TextView tv_show;
	private ListViewAdapter adapter;
	private String[] indexStr = { "#", "A", "B", "C", "D", "E", "F", "G", "H",
			"I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z" };
	private List<Person> persons = null;
	private List<Person> newPersons = new ArrayList<Person>();
	private int height;// ����߶�
	private boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ������
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
		for (int j = 0; j < indexStr.length; j++) {// ѭ����ĸ���ҳ�newPersons�ж�Ӧ��ĸ��λ��
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
	 * ����������һ���µ�List����
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
		// ��oncreate����ִ������Ĵ���û��Ӧ����Ϊoncreate����õ���getHeight=0
		if (!flag) {// ������Activityʧȥ������߱�����ʱ��ÿ�ν��붼���ٴ�ִ�У��ǵ���ĸ�������Ǳ仯�����Լ��ϱ�ǣ�ʹ��ִֻ��һ��
			height = layoutIndex.getMeasuredHeight() / indexStr.length;
			getIndexView();
			flag = true;
		}
	}

	/**
	 * ��ȡ������������
	 * 
	 * @param persons
	 * @return
	 */
	public String[] sortIndex(List<Person> persons) {
		TreeSet<String> set = new TreeSet<String>();
		// ��ȡ��ʼ������Դ�е�����ĸ����ӵ�set��
		for (Person person : persons) {
			set.add(StringHelper.getPinYinHeadChar(person.getName()).substring(
					0, 1));
		}
		// ������ĳ���Ϊԭ���ݼ���set�Ĵ�С
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
		// ��ԭ���ݿ�������������
		System.arraycopy(pinYinNames, 0, names, set.size(), pinYinNames.length);
		// �Զ���������ĸ����
		Arrays.sort(names, String.CASE_INSENSITIVE_ORDER);
		return names;
	}

	/**
	 * ���������б�
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
					if (index > -1 && index < indexStr.length) {// ��ֹԽ��
						String key = indexStr[index];
						if (selector.containsKey(key)) {
							int pos = selector.get(key);
							if (listView.getHeaderViewsCount() > 0) {// ��ֹListView�б�������������û�С�
								listView.setSelectionFromTop(
										pos + listView.getHeaderViewsCount(), 0);
							} else {
								listView.setSelectionFromTop(pos, 0);// ��������һ��
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
	 * ����ģ������
	 */
	private void setData() {
		persons = new ArrayList<Person>();
		Person p1 = new Person("����");
		persons.add(p1);
		Person p2 = new Person("����ǿ");
		persons.add(p2);
		Person p3 = new Person("����");
		persons.add(p3);
		Person p4 = new Person("����");
		persons.add(p4);
		Person p5 = new Person("�����P");
		persons.add(p5);
		Person p6 = new Person("�");
		persons.add(p6);
		Person p7 = new Person("��ܰ��");
		persons.add(p7);
		Person p8 = new Person("����");
		persons.add(p8);
		Person p9 = new Person("����");
		persons.add(p9);
		Person p10 = new Person("����");
		persons.add(p10);
		Person p11 = new Person("������");
		persons.add(p11);
		Person p12 = new Person("����");
		persons.add(p12);
		Person p13 = new Person("����");
		persons.add(p13);
		Person p14 = new Person("�ɻ�");
		persons.add(p14);
		Person p15 = new Person("�̻�");
		persons.add(p15);
		Person p16 = new Person("�ۺ�");
		persons.add(p16);
		Person p17 = new Person("������");
		persons.add(p17);
		Person p18 = new Person("��ҧ��");
		persons.add(p18);
		Person p19 = new Person("�̽�");
		persons.add(p19);
		Person p20 = new Person("���˰�");
		persons.add(p20);
		Person p21 = new Person("����");
		persons.add(p21);

	}

}
