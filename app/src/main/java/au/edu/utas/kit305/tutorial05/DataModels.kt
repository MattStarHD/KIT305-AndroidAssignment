package au.edu.utas.kit305.tutorial05

data class House(
    val id: String = "",
    val houseName: String = "",
    val address: String = "",
    val customerName: String = "",
    val total: Double = 0.0,
    val status: String = "Draft"
)

data class Room(
    val id: String = "",
    val houseId: String = "",
    val roomName: String = "",
    val width: Double = 0.0,
    val depth: Double = 0.0,
    val notes: String = "",
)

data class Floor(
    val id: String = "",
    val roomId: String = "",
    val productId: String = "",
    val notes: String = "",
    val width: Double = 0.0,
    val depth: Double = 0.0,
    val colour: String = "",
    val area: Double = 0.0,
    val totalPrice: Double = 0.0,
)

data class Window(
    val id: String = "",
    val roomId: String = "",
    val productId: String = "",
    val notes: String = "",
    val width: Double = 0.0,
    val height: Double = 0.0,
    val colour: String = "",
    val area: Double = 0.0,
    val totalPrice: Double = 0.0
    )

data class Product(
    val id: String = "",
    val type: String = "",
    val name: String = "",
    val pricePerSquareMeter: Double = 0.0,
    val description: String = "",
    val variants: List<String> = emptyList()
)

data class RoomItem(
    val name: String = "",
    val details: String = "",
    val price: Double = 0.0
)