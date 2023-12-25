package ru.practicum.shareit.booking.model;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import javax.annotation.processing.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QBooking is a Querydsl query type for Booking
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBooking extends EntityPathBase<Booking> {

    private static final long serialVersionUID = 551248612L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBooking booking = new QBooking("booking");

    public final ru.practicum.shareit.user.model.QUser booker;

    public final DateTimePath<java.time.LocalDateTime> end = createDateTime("end", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ru.practicum.shareit.item.model.QItem item;

    public final DateTimePath<java.time.LocalDateTime> start = createDateTime("start", java.time.LocalDateTime.class);

    public final EnumPath<ru.practicum.shareit.booking.BookingStatus> status = createEnum("status", ru.practicum.shareit.booking.BookingStatus.class);

    public QBooking(String variable) {
        this(Booking.class, forVariable(variable), INITS);
    }

//    public QBooking(Path<? extends Booking> path) {
//        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
//    }
//
//    public QBooking(PathMetadata metadata) {
//        this(metadata, PathInits.getFor(metadata, INITS));
//    }
//
//    public QBooking(PathMetadata metadata, PathInits inits) {
//        this(Booking.class, metadata, inits);
//    }

    public QBooking(Class<? extends Booking> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.booker = inits.isInitialized("booker") ? new ru.practicum.shareit.user.model.QUser(forProperty("booker")) : null;
        this.item = inits.isInitialized("item") ? new ru.practicum.shareit.item.model.QItem(forProperty("item"), inits.get("item")) : null;
    }

}

