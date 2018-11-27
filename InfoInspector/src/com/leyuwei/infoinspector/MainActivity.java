package com.leyuwei.infoinspector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class MainActivity extends Activity {
	
	private static final String file = "data.xls";
	private static final String fileDir = "/InfoInspector";
	private static final String fileFullDir = "/InfoInspector/data.xls";
	private Button btn_search;
	private EditText ed;
	private TextView tv_info;
	private RadioButton rb1,rb2;
	private ListView lv;
	private SimpleAdapter adaptor;
	private List<Map<String, Object>> datalist;
	private List<InfoItem> database;
	private boolean isDatabaseLoaded = false;
	private ProgressDialog pd;
	private SharedPreferences sp;
	private Editor editor;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Welcome to new version!
		sp = this.getSharedPreferences("infoinspect", Context.MODE_PRIVATE);
		editor = sp.edit();
		String welcomeStr = "欢迎使用新版本\n更新内容包括：\n* 新增对于Android4以上设备的兼容支持\n* 修正查询结果标题重复的问题\n* 新增对内置存储空间的读取支持";
		String versionStr = "isNew1127";
		if (sp.getBoolean(versionStr, true)) {
			alert(welcomeStr);
			editor.putBoolean(versionStr, false);
			editor.commit();
		}
		
		// Load Database into private variables
		boolean isExternalDatabaseExists = false;
		pd = new ProgressDialog(this);
		pd.setCancelable(false);
		pd.setMessage("正在识别外部数据库......");
		pd.show();
		if (!FileUtils.fileExists(fileDir, this)) {
			String dir = FileUtils.createDir(fileDir, this);
			alert("已为您创建文件夹: " + dir + "\n现在您可以将数据库data.xls放入其中，APP会自动优先加载！");
		} else {
			if (FileUtils.fileExists(fileFullDir, this)) 
				isExternalDatabaseExists = true;
		}
		pd.cancel();
		pd.setMessage("正在加载数据库......");
		pd.show();
		database = new ArrayList<InfoItem>();
		try {
			loadDataFromXls(isExternalDatabaseExists);
			isDatabaseLoaded = true;
			pd.cancel();
		} catch (BiffException e) {
			pd.cancel();
			alert("数据库加载遇到问题，请检查APP完整性！");
			e.printStackTrace();
		} catch (IOException e) {
			pd.cancel();
			alert("数据库加载遇到问题，请检查APP完整性！");
			e.printStackTrace();
		}
		if (isExternalDatabaseExists)
			Toast.makeText(getBaseContext(), "已成功加载外部数据库", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(getBaseContext(), "已成功加载内置数据库", Toast.LENGTH_SHORT).show();
		
		// Initial Widgets
		btn_search = (Button) findViewById(R.id.button1);
		ed = (EditText) findViewById(R.id.editText1);
		ed.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
		rb1 = (RadioButton) findViewById(R.id.radio0);
		rb2 = (RadioButton) findViewById(R.id.radio1);
		lv = (ListView) findViewById(R.id.listView1);
		tv_info = (TextView) findViewById(R.id.textView4);
		tv_info.setClickable(true);
		final String dataDir = FileUtils.createDir(fileDir, this);
		tv_info.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alert("您可以将数据库文件data.xls放入手机存储的InfoInspector文件夹下，本软件会自动读取并覆盖现有数据库，以便您可以方便地处理数据库更新。\n您的数据文件夹是：" + dataDir);
			}
		});
		btn_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int floor = Integer.parseInt(ed.getEditableText().toString());
				if ( floor!=0 && floor>-3 && floor<35 ) {
					if (isDatabaseLoaded) {
						// Search is too fast.
						// Toast.makeText(MainActivity.this, "开始搜索，请等候...", Toast.LENGTH_SHORT).show();
						int option = 0;
						if (rb1.isChecked())
							option = 0;
						else
							option = 1;
						doSearch(floor, option);
						adaptor.notifyDataSetChanged(); // Refresh the datalist
					} else {
						alert("数据库未正确加载！");
					}
				} else {
					alert("楼层数输入不正确！\n请重新输入");
					ed.setText("");
				}
			}
		});
		datalist = new ArrayList<Map<String, Object>>();
		adaptor = new SimpleAdapter(this, datalist, R.layout.tab_listview_item, 
							new String[] {"name", "loc", "quote"}, new int[] {R.id.tv_name, R.id.tv_location, R.id.tv_quote});
		lv.setAdapter(adaptor);
	}
	
	
	private void doSearch(int floor, int option) {
		datalist.clear();
		int result_count = 0;
		for (InfoItem i : database) {
			if(i.option == option && i.floor == floor) {
				Map<String, Object> map = new HashMap<String, Object>();
	            map.put("name", "名称： " + i.name);
	            map.put("loc", "地点： " + i.location);
	            map.put("quote", "备注： " + i.quote);
	            datalist.add(map);
	            result_count++;
			}
		}
		if (result_count==0)
			alert("该楼层没有搜索结果");
	}
	
	
	private void alert(String str) {
		AlertDialog.Builder ad = new AlertDialog.Builder(this);
		ad.setTitle("提示");
		ad.setMessage(str);
		ad.setPositiveButton("我知道了", null);
		ad.show();
	}
	
	
	private void loadDataFromXls(boolean isExternalDatabaseExists) throws BiffException, IOException{
		Workbook wb;
		if (!isExternalDatabaseExists) {
			AssetManager assetManager = getAssets();
			wb = Workbook.getWorkbook(assetManager.open(file));
		} else {
			wb = Workbook.getWorkbook(FileUtils.getFileStream(fileFullDir, this));
		}
		
		// Start Reading Electronic Devices' List (Sheet0)
		Sheet sheet = wb.getSheet(0);
		Range[] rangeCell = sheet.getMergedCells();
		for (int i = 1; i < sheet.getRows(); i++) {
			InfoItem item = new InfoItem();
			item.option = 0; // Tag Electronic Device
			for (int j = 1; j < sheet.getColumns(); j++) {
				String str = null;
				str = sheet.getCell(j, i).getContents();
				for (Range r : rangeCell) {
					if (i > r.getTopLeft().getRow()
							&& i <= r.getBottomRight().getRow()
							&& j >= r.getTopLeft().getColumn()
							&& j <= r.getBottomRight().getColumn()) {
						str = sheet.getCell(r.getTopLeft().getColumn(),
								r.getTopLeft().getRow()).getContents();
					}
				}
				switch (j) {
				case 1:
					item.floor = Integer.parseInt(str);
					break;
				case 2:
					item.name = str;
					break;
				case 3:
					item.location = str;
					break;
				case 4:
					item.quote = str;
					break;
				}
			}
			database.add(item); // Add to database
		}
		
		// Start Reading Water Devices' List (Sheet2)
		sheet = wb.getSheet(2);
		rangeCell = sheet.getMergedCells();
		for (int i = 1; i < sheet.getRows(); i++) {
			InfoItem item = new InfoItem();
			item.option = 1; // Tag Electronic Device
			for (int j = 1; j < sheet.getColumns(); j++) {
				String str = null;
				str = sheet.getCell(j, i).getContents();
				for (Range r : rangeCell) {
					if (i > r.getTopLeft().getRow()
							&& i <= r.getBottomRight().getRow()
							&& j >= r.getTopLeft().getColumn()
							&& j <= r.getBottomRight().getColumn()) {
						str = sheet.getCell(r.getTopLeft().getColumn(),
								r.getTopLeft().getRow()).getContents();
					}
				}
				switch (j) {
				case 1:
					item.floor = Integer.parseInt(str);
					break;
				case 2:
					item.name = str;
					break;
				case 3:
					item.location = str;
					break;
				case 4:
					item.quote = str;
					break;
				}
			}
			database.add(item); // Add to database
		}
		wb.close(); // Close File
	}

	
}
