package bluebox.scr.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bluebox.scr.R;
import bluebox.scr.fragment.BookDetailFragment;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FullScreenActivity extends Activity implements OnClickListener
{
	private List<String> _fileList = new ArrayList<String>();
	private String _currentPath;
	private int _currentZoom = 1;
	private float _currentPage = 0;
	private int _maxPage;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        
        ((Button) findViewById(R.id.btPrev)).setOnClickListener(this);
        ((Button) findViewById(R.id.btNext)).setOnClickListener(this);
        ((Button) findViewById(R.id.btIn)).setOnClickListener(this);
        ((Button) findViewById(R.id.btOut)).setOnClickListener(this);
        
        _currentPath = getIntent().getStringExtra(BookDetailFragment.ARG_ITEM_PATH);
        
        scanBookPages();
        
		refreshInterface();
        redrawPage();
    }

	private void redrawPage() {
		int fileIndex = 0;
		switch (_currentZoom)
		{
		case 1: fileIndex = (int)_currentPage; break;
		case 2: fileIndex = (int)_currentPage / 2; break;
		case 3: fileIndex = (int)_currentPage / 4; break;
		}
		
		Bitmap source = BitmapFactory.decodeFile(_fileList.get(fileIndex));
		Bitmap output = null;
		
		switch (_currentZoom)
		{
		case 1: output = source; break;
		case 2: output = Bitmap.createBitmap(source, 0, (int)_currentPage % 2 == 0 ? 0 : source.getHeight() / 2, source.getWidth(), source.getHeight() / 2); break; 
		case 3: output = Bitmap.createBitmap(source, 0, 0, source.getWidth() / 2, source.getHeight() / 2); break;
		}
		 
		((ImageView) findViewById(R.id.imageView)).setImageBitmap(output);
		
		if (source != output)
			source.recycle();
	}

	private boolean isImage(File f)
	{
		String name = f.getName().toLowerCase();
		if (name.endsWith(".jpg")) return true;
		if (name.endsWith(".jpeg")) return true;
		if (name.endsWith(".gif")) return true;
		if (name.endsWith(".png")) return true;
		if (name.endsWith(".bmp")) return true;
		
		return false;
	}
	
    private void scanBookPages() {
        File root = new File(_currentPath);
        
        File[] files = root.listFiles();
        for (File file : files)
        {
        	if (isImage(file))
        	{
        		_fileList.add(file.getPath());
        	}
        }
        
        Collections.sort(_fileList, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        
        _maxPage = _fileList.size();
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_full_screen, menu);
        return true;
    }

	public void onClick(View v) {
		switch (v.getId())
		{
			case R.id.btPrev: if (_currentPage > 0) _currentPage--; break;
			case R.id.btNext: if (_currentPage + 1 < _maxPage)_currentPage++; break;
			case R.id.btIn: if (_currentZoom < 3) { _currentZoom++; _currentPage *= 2; _maxPage *= 2; } break;
			case R.id.btOut: if (_currentZoom > 1) { _currentZoom--; _currentPage /= 2; _maxPage /= 2; } break;
		}
		
		refreshInterface();

		redrawPage();
	}

	private void refreshInterface() {
		((TextView) findViewById(R.id.lbPage)).setText(String.format("Page: %d", (int)_currentPage));
		((TextView) findViewById(R.id.lbZoom)).setText(String.format("Zoom: %d", _currentZoom));
	}

}
