package ru.ulstu.`is`.pmu.database.task.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks", foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.RESTRICT
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "endDate")
    val endDate: String,
    @ColumnInfo(name = "favorite")
    var favorite: Boolean,
    @ColumnInfo(name = "user_id", index = true)
    val userId: Int,
) {

    @Ignore
    constructor(
        name: String,
        description: String,
        endDate: String,
        favorite: Boolean,
        user: User,
    ) : this(0, name, description, endDate, favorite, 1)


    companion object {
        fun getTask(index: Int = 0): Task {
            return Task(
                index,
                "Test1234567",
                "Test1235",
                "11.12.2023",
                true,
                0,
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Task
        if (uid != other.uid) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (endDate != other.endDate) return false
        if (favorite != other.favorite) return false
        if (userId != other.userId) return false
        return true
    }

    override fun hashCode(): Int {
        var result = uid
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + endDate.hashCode()
        result = 31 * result + favorite.hashCode()
        result = 31 * result + userId
        return result
    }
}
