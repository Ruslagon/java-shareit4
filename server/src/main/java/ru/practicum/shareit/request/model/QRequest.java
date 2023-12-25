package ru.practicum.shareit.request.model;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import javax.annotation.processing.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QRequest is a Querydsl query type for Request
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRequest extends EntityPathBase<Request> {

    private static final long serialVersionUID = 434774224L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRequest request = new QRequest("request");

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<ru.practicum.shareit.item.model.Item, ru.practicum.shareit.item.model.QItem> items = this.<ru.practicum.shareit.item.model.Item, ru.practicum.shareit.item.model.QItem>createList("items", ru.practicum.shareit.item.model.Item.class, ru.practicum.shareit.item.model.QItem.class, PathInits.DIRECT2);

    public final ru.practicum.shareit.user.model.QUser user;

    public QRequest(String variable) {
        this(Request.class, forVariable(variable), INITS);
    }

//    public QRequest(Path<? extends Request> path) {
//        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
//    }
//
//    public QRequest(PathMetadata metadata) {
//        this(metadata, PathInits.getFor(metadata, INITS));
//    }
//
//    public QRequest(PathMetadata metadata, PathInits inits) {
//        this(Request.class, metadata, inits);
//    }

    public QRequest(Class<? extends Request> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new ru.practicum.shareit.user.model.QUser(forProperty("user")) : null;
    }

}

