package com.example.service.persistence.jooq;

import com.example.service.application.command.WidgetConflictException;
import com.example.service.application.port.WidgetRepository;
import com.example.service.domain.Widget;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

/** Small manual jOOQ mapping that keeps generated-code setup out of first use. */
public final class JooqWidgetRepository implements WidgetRepository {
  private static final Table<?> WIDGETS = DSL.table(DSL.name("widgets"));
  private static final Field<UUID> ID = DSL.field(DSL.name("id"), UUID.class);
  private static final Field<String> NAME = DSL.field(DSL.name("name"), String.class);
  private static final Field<OffsetDateTime> CREATED_AT =
      DSL.field(DSL.name("created_at"), OffsetDateTime.class);

  private final DSLContext dsl;

  /** Creates the adapter with Spring Boot's configured jOOQ context. */
  public JooqWidgetRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  @Override
  public Widget save(Widget widget) {
    try {
      dsl.insertInto(WIDGETS)
          .columns(ID, NAME, CREATED_AT)
          .values(
              widget.id(),
              widget.name(),
              OffsetDateTime.ofInstant(widget.createdAt(), ZoneOffset.UTC))
          .execute();
      return widget;
    } catch (DataAccessException exception) {
      if ("23505".equals(exception.sqlState())) {
        throw new WidgetConflictException("A widget with that name already exists", exception);
      }
      throw exception;
    }
  }

  @Override
  public Optional<Widget> findById(UUID id) {
    return dsl.select(ID, NAME, CREATED_AT)
        .from(WIDGETS)
        .where(ID.eq(id))
        .fetchOptional(this::toDomain);
  }

  @Override
  public List<Widget> findAll(int limit) {
    return dsl.select(ID, NAME, CREATED_AT)
        .from(WIDGETS)
        .orderBy(CREATED_AT.desc())
        .limit(limit)
        .fetch(this::toDomain);
  }

  private Widget toDomain(org.jooq.Record record) {
    return new Widget(record.get(ID), record.get(NAME), record.get(CREATED_AT).toInstant());
  }
}
