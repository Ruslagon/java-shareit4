package ru.practicum.shareit.comment.model;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;

import javax.annotation.processing.Generated;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = -1651823504L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComment comment = new QComment("comment");

    public final ru.practicum.shareit.user.model.QUser author;

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ru.practicum.shareit.item.model.QItem item;

    public final StringPath text = createString("text");

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

//    public QComment(Path<? extends Comment> path) {
//        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
//    }
//
//    public QComment(PathMetadata metadata) {
//        this(metadata, PathInits.getFor(metadata, INITS));
//    }
//
//    public QComment(PathMetadata metadata, PathInits inits) {
//        this(Comment.class, metadata, inits);
//    }

    public QComment(Class<? extends Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new ru.practicum.shareit.user.model.QUser(forProperty("author")) : null;
        this.item = inits.isInitialized("item") ? new ru.practicum.shareit.item.model.QItem(forProperty("item"), inits.get("item")) : null;
    }

}

