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

import bluebox.SmartComicsReader.Fragment.BookDetailFragment;
import bluebox.SmartComicsReader.Fragment.BookListFragment;
import bluebox.SmartComicsReader.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BookListActivity extends FragmentActivity implements BookListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        if (findViewById(R.id.book_detail_container) != null) {
            mTwoPane = true;
            ((BookListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.book_list))
                    .setActivateOnItemClick(true);
        }
    }

//    @Override
    public void onItemSelected(String path, String name) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(BookDetailFragment.ARG_ITEM_NAME, name);
            arguments.putString(BookDetailFragment.ARG_ITEM_PATH, path);
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.book_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, BookDetailActivity.class);
            detailIntent.putExtra(BookDetailFragment.ARG_ITEM_NAME, name);
            detailIntent.putExtra(BookDetailFragment.ARG_ITEM_PATH, path);
            startActivity(detailIntent);
        }
    }
}
