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

package bluebox.SmartComicsReader.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bluebox.SmartComicsReader.Data.Book;
import bluebox.SmartComicsReader.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BookListFragment extends ListFragment
{
    private List<Book> _bookList;
	
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private Callbacks mCallbacks;
    private int mActivatedPosition = ListView.INVALID_POSITION;

    public interface Callbacks
    {
        public void onItemSelected(String id, String name);
    }

    public BookListFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        scanCollection();
        
        setListAdapter(new ArrayAdapter<Book>(getActivity(),
                R.layout.simple_list_item_activated_1,
                R.id.text1,
                _bookList));
    }

    private void scanCollection() {
    	_bookList = new ArrayList<Book>();

        File root = new File(Environment.getExternalStorageDirectory().getPath() + "/bd/");
        
        File[] files = root.listFiles();
        for (File file : files)
        {
        	_bookList.add(new Book(file));
        }
        
        Collections.sort(_bookList, new Comparator<Book>() {
            public int compare(Book b1, Book b2) {
                return b1.name.compareToIgnoreCase(b2.name);
            }
        });

    }

	@Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION))
        {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks))
        {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id)
    {
        super.onListItemClick(listView, view, position, id);
        if (mCallbacks != null)
        	mCallbacks.onItemSelected(_bookList.get(position).path, _bookList.get(position).name);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION)
        {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    public void setActivateOnItemClick(boolean activateOnItemClick)
    {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    public void setActivatedPosition(int position)
    {
        if (position == ListView.INVALID_POSITION)
        {
            getListView().setItemChecked(mActivatedPosition, false);
        }
        else
        {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
