# Interior Design Quoting App

Emulator: Pixel 9a (API 35)


References:
- Unit tutorials and lecture material
- Firebase Firestore documentation
- Android developer documentation
- JSONObject documentation
- RecyclerView documentation


Generative AI Usage

- Helped with input validation (ensuring dimension values were numeric)
- elped with making the quote screen
- Made improvements with UI and consistency across screens
- Helped with debugging

https://chatgpt.com/share/69f4a393-0a04-83ea-9fef-d6029d66f5be
https://chatgpt.com/share/69f4a543-21f0-83ea-ba7c-1c95e381de43
https://chatgpt.com/share/69f4a627-09a0-83ea-81b8-1cf1e3440e99


App Activities

The user starts in MainActivity, there they select or create a house and view its rooms 
in HouseDetailsActivity. They then select the room they'd like to focus on and are 
given the floor and window options with their respective dimensions, users can add 
or edit items from there. They also have access to the quote page to see the full 
house with all its items, and has the ability to select and deselect rooms.

- MainActivity  
  Displays a list of houses

- AddHouseActivity / EditHouseActivity  
  Create and edit house details

- HouseDetailsActivity
  Displays rooms within a selected house

- AddRoomActivity / EditRoomActivity
  Create and edit rooms

- RoomDetailsActivity
  Displays floors and windows for a room

- AddFloorActivity / EditFloorActivity  
  Create and edit floor spaces

- AddWindowActivity / EditWindowActivity
  Allows the user to create and edit windows

- ProductSelectorActivity  
  Allows user to select a product from a JSON file

- ColourSelectorActivity
  Allows user to select a colour for a product from the JSON file

- QuoteActivity  
  Displays the total cost for a house and allows for specific rooms or items to be selected

