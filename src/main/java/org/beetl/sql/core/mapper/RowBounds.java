package org.beetl.sql.core.mapper;

import org.beetl.sql.core.kit.NumberKit;

/**
 * RowBounds.(from mybatis).
 * 
 * @author zhoupan
 */
public class RowBounds {

	/** The Constant NO_ROW_OFFSET. */
	public static final int NO_ROW_OFFSET = 0;

	/** The Constant NO_ROW_LIMIT. */
	public static final int NO_ROW_LIMIT = Integer.MAX_VALUE;

	/** The Constant DEFAULT. */
	public static final RowBounds DEFAULT = new RowBounds();

	/** The offset. */
	private int offset;

	/** The limit. */
	private int limit;

	/**
	 * The Constructor.
	 */
	public RowBounds() {
		offset = 0;
		limit = Integer.MAX_VALUE;
	}

	/**
	 * The Constructor.
	 *
	 * @param offset
	 *            the offset
	 * @param limit
	 *            the limit
	 */
	public RowBounds(int offset, int limit) {
		this.offset = offset;
		this.limit = limit;
	}

	/**
	 * Gets the offset.
	 *
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Gets the limit.
	 *
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * Offset.
	 *
	 * @param offset
	 *            the offset
	 * @return the row bounds
	 */
	public RowBounds offset(Object offset) {
		if (offset != null) {
			this.offset = NumberKit.parseNumber(offset.toString(), Integer.class);
		}
		return this;
	}

	/**
	 * Offset.
	 *
	 * @param offset
	 *            the offset
	 * @return the row bounds
	 */
	public RowBounds offset(int offset) {
		if (offset >= 0) {
			this.offset = offset;
		}
		return this;
	}

	/**
	 * Limit.
	 *
	 * @param limit
	 *            the limit
	 * @return the row bounds
	 */
	public RowBounds limit(Object limit) {
		if (limit != null) {
			this.limit = NumberKit.parseNumber(limit.toString(), Integer.class);
		}
		return this;
	}

	/**
	 * Limit.
	 *
	 * @param limit
	 *            the limit
	 * @return the row bounds
	 */
	public RowBounds limit(int limit) {
		if (limit >= 0) {
			this.limit = limit;
		}
		return this;
	}

}