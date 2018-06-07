# PopularMovies
Project 2 for Udacity Android Developer Nanodegree

The Popular Movies app presents the user with movie posters on launch. The user can 
switch between most popular and highest rated movies. When user clicks on a poster,
detail view is launched to display the movies info, such as title, year, rating, synopsis,
trailers and reviews. User can add the movie to local database for viewing some details
offline.

### Projects requirements:
- query themoviedb.org API for movie info
- allow the user to choose between top rated or most popular movies
- display movie posters in a grid arrangement
- upon a poster click, launch details view
- on trailer click, launch trailer on Youtube
- persist state
- store favorites in native SQLite database, using ContentProvider

By completing this project, the student demonstrates understanding of the foundational 
elements of Android by building compelling UI, fetching data from network services and
optimizing the experience for various mobile devices.



### Additional features:
- Performance
  - RecyclerView instead of ListView for better performance for poster grid, trailers view,
    reviews view
  - Loaders instead of AsyncTask for querying APIs
  - AsynchTask with weak references as inner classes for inserting in / deleting from local
    db, instead of leaving these actions on the main Thread
- UI
  - BottomNavigation
  - CollapsingToolbarLayout
  - Grid view span based on devices width
  - CoordinatorLayout with AppbarLayout to hide Toolbar on scroll
  - Query Youtube API for trailer thumbnails to display them in CardViews, instead of
    just listing video titles
  - Review items are expanded / collapsed on click
