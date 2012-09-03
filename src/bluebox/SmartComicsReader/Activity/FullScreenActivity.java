/*
 * Copyright 2011 Alexis Lauper <alexis.lauper@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package bluebox.SmartComicsReader.Activity;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bluebox.SmartComicsReader.R;
import bluebox.SmartComicsReader.Fragment.BookDetailFragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

public class FullScreenActivity extends Activity implements OnClickListener, OnTouchListener
{
	private List<String>			_fileList = new ArrayList<String>();
	private String 					_currentPath;
	private int 					_currentZoom = 1;
	private float 					_currentPage = 0;
	private int 					_maxPage;
	private ImageView				_imageViewPrev;
	private ImageView				_imageViewCurrent;
	private ImageView				_imageViewNext;
	private HorizontalScrollView	_imageScroll;
	private int						_lastX;
	private int						_lastY;
	private boolean 				_onScroll;
	private int 					_direction;
	private boolean _ready = true;
	private List<ImageView> _imageList;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        
        ((Button) findViewById(R.id.btPrev)).setOnClickListener(this);
        ((Button) findViewById(R.id.btNext)).setOnClickListener(this);
        ((Button) findViewById(R.id.btIn)).setOnClickListener(this);
        ((Button) findViewById(R.id.btOut)).setOnClickListener(this);

        _imageScroll = ((HorizontalScrollView) findViewById(R.id.imageScroll));
        _imageScroll.setOnTouchListener(this);
        
        
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)((FrameLayout)findViewById(R.id.pagePrev)).getLayoutParams();
        params.width = getWindowManager().getDefaultDisplay().getWidth();
        
        ((FrameLayout) findViewById(R.id.pagePrev)).setLayoutParams(params);
        ((FrameLayout) findViewById(R.id.pagePrev)).setVisibility(View.GONE);
        ((FrameLayout) findViewById(R.id.pageCurrent)).setLayoutParams(params);
        ((FrameLayout) findViewById(R.id.pageNext)).setLayoutParams(params);
        
        _imageViewPrev = ((ImageView) findViewById(R.id.imageViewPrev));
        _imageViewCurrent= ((ImageView) findViewById(R.id.imageViewCurrent));
        _imageViewNext = ((ImageView) findViewById(R.id.imageViewNext));

        _imageList = new ArrayList<ImageView>();
        
		Bitmap empty = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_search);
        
        for (int i = 0; i < 30; i++)
        {
        	FrameLayout layout = new FrameLayout(this);
        	layout.setLayoutParams(params);
        	
        	ImageView image = new ImageView(this);
        	image.setImageBitmap(empty);
        	layout.addView(image);
        	_imageList.add(image);
        	((LinearLayout) findViewById(R.id.imageContainer)).addView(layout);
        }
        
        //_imageView.setOnTouchListener(this);
        
        _currentPath = getIntent().getStringExtra(BookDetailFragment.ARG_ITEM_PATH);
        
        scanBookPages();
        
		refreshInterface();
        redrawPage();
    }

	private void redrawPage() {
		
		for (int i = (int)_currentPage - 1; i <= (int)_currentPage + 1; i++)
		{
			if (i >= 0 && i < _maxPage)
			{
				ImageView image = _imageList.get(i);
				drawPage(image, i);
			}
		}
		
//		switch (_direction)
//		{
//		case View.FOCUS_LEFT:
//			_imageViewNext.setImageDrawable(_imageViewCurrent.getDrawable());
//			_imageViewCurrent.setImageDrawable(_imageViewPrev.getDrawable());
//			if (_currentPage - 1 >= 0) drawPage(_imageViewPrev, _currentPage - 1);
//			break;
//		case View.FOCUS_RIGHT:
//			_imageViewPrev.setImageDrawable(_imageViewCurrent.getDrawable());
//			_imageViewCurrent.setImageDrawable(_imageViewNext.getDrawable());
//			if (_currentPage + 1 < _maxPage) drawPage(_imageViewNext, _currentPage + 1);
//			break;
//		default:
//			if (_currentPage - 1 >= 0) drawPage(_imageViewPrev, _currentPage - 1);
//			if (_currentPage + 1 < _maxPage) drawPage(_imageViewNext, _currentPage + 1);
//			drawPage(_imageViewCurrent, _currentPage);
//		}
//		_direction = 0;
	}

	private void drawPage(ImageView image, float page) {
		int fileIndex = 0;
		switch (_currentZoom)
		{
		case 1: fileIndex = (int)page; break;
		case 2: fileIndex = (int)page / 2; break;
		case 3: fileIndex = (int)page / 4; break;
		}
		
		Bitmap source = BitmapFactory.decodeFile(_fileList.get(fileIndex));
		Bitmap output = null;
		
		switch (_currentZoom)
		{
		case 1: output = source; break;
		case 2: output = Bitmap.createBitmap(source, 0, (int)page % 2 == 0 ? 0 : source.getHeight() / 2, source.getWidth(), source.getHeight() / 2); break; 
		case 3: output = Bitmap.createBitmap(source, 0, 0, source.getWidth() / 2, source.getHeight() / 2); break;
		}

		image.setImageBitmap(output);
		
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

	public boolean onTouch(View v, MotionEvent event) {
//    	Log.w("bluebox.scr", String.format("%d %d", _imageScroll.getScrollX(), display.getWidth()));

		final Display display = getWindowManager().getDefaultDisplay();

		switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
            	_lastX = (int)event.getX();
            	_lastY = (int)event.getY();
        		break;
            }

            case MotionEvent.ACTION_UP:
            {
            	//Log.w("bluebox.scr", String.format("%d %d", (int)event.getX(), (int)_lastX));
            	
            	if (Math.abs(event.getX() - _lastX) > 50)
            			//&& Math.abs(event.getX() - _lastX) > Math.abs(event.getY() - _lastY))
            	{
            		if (event.getX() - _lastX > 0)
            		{
            			//_imageScroll.fullScroll(View.FOCUS_LEFT);
            			if (_currentPage > 0) _currentPage--;
            			else
        				{
            				Log.w("bluebox.scr", "wrong");
        					return false;
        				}
            		}
            		else
            		{
            			//_imageScroll.fullScroll(View.FOCUS_RIGHT);
            			if (_currentPage + 1 < _maxPage) _currentPage++;
            			else
        				{
            				Log.w("bluebox.scr", "wrong");
            				return false;
        				}
            		}
            		
            		//_imageScroll.scrollTo((int)_currentPage * display.getWidth(), 0);

            		//Flinger flinger = new Flinger();
            		//flinger.start(50000);
            		
            		final int startPos = _imageScroll.getScrollX();
            		final int endPos = (int)(_currentPage * display.getWidth());
            		final int offset = (startPos - endPos) / 100;
            		
            		Log.w("bluebox.scr", "start: " + startPos);
            		Log.w("bluebox.scr", "end: " + endPos);
            		
            	    new CountDownTimer(2000, 20) { 

            	    	float _pos = startPos;
            	    	
            	        public void onTick(long millisUntilFinished) { 
                    		_imageScroll.scrollTo((int)_pos, 0);
                    		_pos += offset;
            	        	//_imageScroll.scrollTo((int) (2000 - millisUntilFinished), 0); 
            	        } 

            	        public void onFinish() { 
        	        		refreshInterface();
        	        		redrawPage();
            	        } 
            	     }.start();

            		return true;
            	}
            }
        }
		return false;
//            		
////        			_imageScroll.postDelayed(new Runnable() {
////						public void run() {
////            	        	int x = -1;
////
////            	        	while (x != _imageScroll.getScrollX())
////            	        	{
////                	        	Log.i("bluebox.scr", String.format("scroll: %d", _imageScroll.getScrollX()));
////                	        	
////            	        		x = _imageScroll.getScrollX();
////            	        		try {
////									Thread.sleep(100);
////								} catch (InterruptedException e) {
////									e.printStackTrace();
////								}
////            	        	}
////            	        	moveToPage();
////                    		refreshInterface();
////                    		redrawPage();
////                    		_ready = true;
////            	        }
////            	    }, 750);
//            	}
//            	// scroll trop petit
//            	else
//            	{
//        			moveToPage();
//            	}
//        		return true;
//            	//break;
//            }
//        }
//        return false;
	}

	protected void moveToPage() {
		final Display display = getWindowManager().getDefaultDisplay();

		if (_currentPage == 0)
    	{
    		((FrameLayout) findViewById(R.id.pagePrev)).setVisibility(View.GONE);
    		_imageScroll.scrollTo(0 , 0);
    	}
		if (_currentPage == _maxPage - 1)
    	{
    		((FrameLayout) findViewById(R.id.pageNext)).setVisibility(View.GONE);
        	_imageScroll.scrollTo(display.getWidth() , 0);
    	}
    	else
    	{
        	((FrameLayout) findViewById(R.id.pagePrev)).setVisibility(View.VISIBLE);
    		((FrameLayout) findViewById(R.id.pageNext)).setVisibility(View.VISIBLE);
        	_imageScroll.scrollTo(display.getWidth() , 0);
    	}
	}
	
	private class Flinger implements Runnable {
	    private final Scroller scroller;

	    private int lastX = 0;

	    Flinger() {
	        scroller = new Scroller(FullScreenActivity.this);
	    }

	    void start(int initialVelocity) {
	        int initialX = _imageScroll.getScrollX();
	        int maxX = Integer.MAX_VALUE; // or some appropriate max value in your code
	        scroller.fling(initialX, 0, initialVelocity, 0, 0, maxX, 0, 10);
	        Log.i("bluebox.scr", "starting fling at " + initialX + ", velocity is " + initialVelocity + "");

	        lastX = initialX;
	        _imageScroll.post(this);
	    }

	    public void run() {
	        if (scroller.isFinished()) {
		        Log.i("bluebox.scr", "finish fling at " + _imageScroll.getScrollX());
	            Log.i("bluebox.scr", "scroller is finished, done with fling");
	            return;
	        }

	        boolean more = scroller.computeScrollOffset();
	        int x = scroller.getCurrX();
	        int diff = lastX - x;
	        if (diff != 0) {
	        	_imageScroll.scrollBy(diff, 0);
	            lastX = x;
	        }

	        if (more) {
	        	_imageScroll.post(this);
	        }
	    }

	    boolean isFlinging() {
	        return !scroller.isFinished();
	    }

	    void forceFinished() {
	        if (!scroller.isFinished()) {
	            scroller.forceFinished(true);
	        }
	    }
	}
}