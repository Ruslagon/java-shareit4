package ru.practicum.shareit.item.model;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import javax.annotation.processing.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QItem is a Querydsl query type for Item
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QItem extends EntityPathBase<Item> {

    private static final long serialVersionUID = 1152785816L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QItem item = new QItem("item");

    public final BooleanPath available = createBoolean("available");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ru.practicum.shareit.user.model.QUser owner;

    public final NumberPath<Long> requestId = createNumber("requestId", Long.class);

    public QItem(String variable) {
        this(Item.class, forVariable(variable), INITS);
    }

//    public QItem(Path<? extends Item> path) {
//        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
//    }
//
//    public QItem(PathMetadata metadata) {
//        this(metadata, PathInits.getFor(metadata, INITS));
//    }

    public QItem(PathMetadata metadata, PathInits inits) {
        this(Item.class, metadata, inits);
    }

    public QItem(Class<? extends Item> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.owner = inits.isInitialized("owner") ? new ru.practicum.shareit.user.model.QUser(forProperty("owner")) : null;
    }

}

