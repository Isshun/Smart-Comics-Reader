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

import bluebox.SmartComicsReader.Activity.FullScreenActivity;
import bluebox.SmartComicsReader.Data.Book;
import bluebox.SmartComicsReader.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class BookDetailFragment extends Fragment implements OnClickListener
{

    public static final String ARG_ITEM_NAME = "item_name";
    public static final String ARG_ITEM_PATH = "item_path";

    private Book _currentBook;

    public BookDetailFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        _currentBook = new Book();
        
        if (getArguments().containsKey(ARG_ITEM_NAME)) _currentBook.name = getArguments().getString(ARG_ITEM_NAME);
        if (getArguments().containsKey(ARG_ITEM_PATH)) _currentBook.path = getArguments().getString(ARG_ITEM_PATH);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_book_detail, container, false);
        if (_currentBook != null)
        {
            ((Button) rootView.findViewById(R.id.button1)).setText(_currentBook.name);
            ((Button) rootView.findViewById(R.id.button1)).setOnClickListener(this);
        }
        return rootView;
    }

	public void onClick(View v) {
		Intent myIntent = new Intent((Context)this.getActivity(), FullScreenActivity.class);
		myIntent.putExtra(ARG_ITEM_PATH, _currentBook.path);
		this.startActivity(myIntent);
	}
}
