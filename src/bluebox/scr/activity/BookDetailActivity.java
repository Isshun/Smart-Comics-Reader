package bluebox.scr.activity;

import bluebox.scr.R;
import bluebox.scr.fragment.BookDetailFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class BookDetailActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(BookDetailFragment.ARG_ITEM_NAME, getIntent().getStringExtra(BookDetailFragment.ARG_ITEM_NAME));
            arguments.putString(BookDetailFragment.ARG_ITEM_PATH, getIntent().getStringExtra(BookDetailFragment.ARG_ITEM_PATH));
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_detail_container, fragment)
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, BookListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
