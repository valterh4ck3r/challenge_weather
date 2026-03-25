e˙ANDROID CHALLENGEWeather Search using MVVM + Open-Meteo
Objective
Build a small Android component that allows the user to search for a city and display its current weather information.
The focus is on architecture, async search logic, state handling and ViewModel design, more than UI styling.
Requirements
UI
A single screen with:
A search bar (EditText) at the top.


A simple area below (TextViews, CardView, Column, etc.) showing:


City name


Temperature


Weather description (from current_weather.weathercode → no need to map to text unless you want extra points)


As the user types, trigger a search after a 500ms debounce.
APIs to Use (Open & Free)
Geocoding (city → coordinates)
GET https://geocoding-api.open-meteo.com/v1/search?name={CITY}
Notes:
The response may contain multiple locations.
You must take ONLY the first result (if the list is empty → show “no results found”).


Fields needed:


results[0].latitude


results[0].longitude


results[0].name


Weather (coordinates → current weather)
GET https://api.open-meteo.com/v1/forecast?latitude={LAT}&longitude={LON}&current_weather=true

Fields needed:
current_weather.temperature


current_weather.weathercode (optional mapping)


Architecture Requirements
Use MVVM with:
A ViewModel exposing UI state via LiveData or StateFlow.


A Repository layer encapsulating API calls.


Coroutines to handle async operations.


Handle and represent clearly:
Loading state


Error state (e.g. network error)


Empty state (no city results)


Success state


The ViewModel should:
Implement debounced search (500ms).


Cancel previous requests when the query changes quickly (e.g., flatMapLatest or manual coroutine cancellation).


Be written in a way that makes it unit-testable (dependencies injected, no Android framework code).


DI may be manual or with Hilt/Dagger (optional).

UI Expectations
UI does not need to be beautiful. A minimal layout is enough:
EditText


Progress indicator


Simple card/text block for results


Error/empty text


Candidate Discussion Topics
After finishing (or while coding), ask the candidate to explain:
Why this architecture?


How debounce + cancellation is implemented.


How states are modeled (sealed classes, data class, etc.).


Threading choices (Dispatchers, structured concurrency).


How they would test the ViewModel logic.



Expected Duration
A strong senior should complete the core functionality in 35–45 minutes.
(Extras like DI or weathercode mapping are optional.)



