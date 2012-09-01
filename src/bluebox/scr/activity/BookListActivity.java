package bluebox.scr.activity;

import bluebox.scr.R;
import bluebox.scr.fragment.BookDetailFragment;
import bluebox.scr.fragment.BookListFragment;
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
