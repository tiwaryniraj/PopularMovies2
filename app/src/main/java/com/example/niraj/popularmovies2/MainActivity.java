package com.example.niraj.popularmovies2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.niraj.popularmovies2.model.Movie;
import com.example.niraj.popularmovies2.utilities.JSONUtils;
import com.example.niraj.popularmovies2.utilities.NetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, MovieAdapter.ListItemClickListener, FetchPopularMoviesTask.AsyncTaskCompleteListener{

    private static final int NUMBER_COLUMNS = 2;
    public static final String MOVIE_KEY = "movies";

    private RecyclerView mMovieRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorTextView;

    private List<Movie> mPopularMovies;
    private boolean sortByPopularity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMovieRecyclerView = findViewById(R.id.movie_card_recycler_view);
        mErrorTextView = findViewById(R.id.error_message_TV);

        //creating list
        mPopularMovies = new ArrayList<>();

        //checking for rotation
        if(savedInstanceState != null && savedInstanceState.containsKey(MOVIE_KEY)) {
            mPopularMovies = savedInstanceState.getParcelableArrayList(MOVIE_KEY);
            createRecyclerView();
        }
        else {
            fetchPopularMovies();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemClicked = item.getItemId();

        if(menuItemClicked == R.id.sort_by) {
            setPopupMenu();
        } else {
            Intent favoritesIntent = new Intent(this, FavoritesActivity.class);
            startActivity(favoritesIntent);
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_KEY, (ArrayList<? extends Parcelable>) mPopularMovies);
    }

    private void fetchPopularMovies() {
        URL movieUrl;

        if(sortByPopularity) {
            movieUrl = NetworkUtils.buildPopularMoviesUrlByPopularity(1);
        }
        else {
            movieUrl = NetworkUtils.buildPopularMoviesUrlByRating(1);
        }

        new FetchPopularMoviesTask(this).execute(movieUrl);
    }

    private void setPopularMoviesList(String moviesResultsString) {
        try {
            mPopularMovies = JSONUtils.getMovieListFromJSON(moviesResultsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setPopupMenu() {
        View menuItemView = findViewById(R.id.sort_by);

        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    private boolean checkForError(String moviesString) {
        if(moviesString == null || moviesString.equals("")) {
            showErrorMessage();
            return true;
        }
        else {
            return false;
        }
    }

    private void showErrorMessage() {
        mMovieRecyclerView.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);

    }

    private void showMovieData() {
        mMovieRecyclerView.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }

    private void createRecyclerView() {
        mGridLayoutManager = new GridLayoutManager(this, NUMBER_COLUMNS);
        mMovieRecyclerView.setLayoutManager(mGridLayoutManager);

        mMovieAdapter = new MovieAdapter(this, this, mPopularMovies);
        mMovieRecyclerView.setAdapter(mMovieAdapter);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {

        int itemClicked= item.getItemId();

        if(itemClicked == R.id.sort_by_popularity_popup) {
            sortByPopularity = true;
        }

        if(itemClicked == R.id.sort_by_review_popup) {
            sortByPopularity = false;
        }
        fetchPopularMovies();
        return false;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(this, MovieDetailActivity.class);

        intent.putExtra(MOVIE_KEY, mPopularMovies.get(clickedItemIndex));

        startActivity(intent);
    }

    @Override
    public void asyncTaskProcessFinish(String movieJSONString) {
        if(checkForError(movieJSONString)) {
            showErrorMessage();
        }
        else {
            setPopularMoviesList(movieJSONString);
            createRecyclerView();
            showMovieData();
        }
    }
}
