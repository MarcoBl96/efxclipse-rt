/*******************************************************************************
 * Copyright (c) 2018 BestSolution.at and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v.2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tom Schindl<tom.schindl@bestsolution.at> - initial API and implementation
 *******************************************************************************/
package org.eclipse.fx.core.log.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.eclipse.fx.core.log.Condition;
import org.eclipse.fx.core.log.FluentLogContext;
import org.eclipse.fx.core.log.FluentLogContext.MutableState;
import org.eclipse.fx.core.log.FluentLogger;
import org.eclipse.fx.core.log.Logger;
import org.eclipse.fx.core.log.Logger.Level;
import org.eclipse.jdt.annotation.NonNull;

@SuppressWarnings("javadoc")
public class FluentLoggerImpl implements FluentLogger {
	@NonNull
	private final Logger logger;

	private static final FluentLogContext NO_LOG = new NoOpLogContextImpl();

	static class NoOpLogContextImpl implements FluentLogContext {
		
		@Override
		public <T> void log(@NonNull Function<T, @NonNull CharSequence> messageSupplier, T type) {
			// empty
		}

		@Override
		public void log(@NonNull Supplier<@NonNull CharSequence> messageSupplier) {
			// empty
		}

		@Override
		public void log(CharSequence message) {
			// empty
		}

		@Override
		public void log(String message, Object... arguments) {
			// empty
		}

		@Override
		public FluentLogContext withException(Throwable t) {
			return this;
		}
		
		@Override
		public FluentLogContext when(Predicate<MutableState> t) {
			return this;
		}
		
		@Override
		public <T extends Condition> T with(Function<FluentLogContext, T> condition) {
			return condition.apply(this);
		}
	}

	static class FluentLogContextImpl implements FluentLogContext {
		@NonNull
		private final Logger logger;
		@NonNull
		private final Level level;

		private Throwable t;
		
		private List<Predicate<MutableState>> whenList;

		private final MutableStateImpl state;
		
		@SuppressWarnings("null")
		@NonNull
		private static final String FQN = FluentLogContextImpl.class.getCanonicalName();

		public FluentLogContextImpl(@NonNull Level level, @NonNull Logger logger) {
			this.level = level;
			this.logger = logger;
			this.state = new MutableStateImpl();
		}
		
		@Override
		public FluentLogContext when(Predicate<MutableState> t) {
			if( this.whenList == null ) {
				this.whenList = new CopyOnWriteArrayList<>();
			}
			this.whenList.add(t);
			return this;
		}
		
		@Override
		public <T extends Condition> T with(Function<FluentLogContext, T> condition) {
			return condition.apply(this);
		}
		
		private boolean shouldLog() {
			this.state.incrementCallCount();
			if( this.whenList == null) {
				return true;
			}
			
			boolean rv = this.whenList == null ? true : this.whenList.stream().mapToInt(w -> w.test(this.state) ? 1 : 0).sum() == this.whenList.size();
			
			if( rv ) {
				this.state.recordLastLog();
			}
			
			return rv;
		}

		@Override
		public void log(CharSequence message) {
			if( shouldLog() ) {
				Throwable t = this.t;
				if (t == null) {
					this.logger.log(FQN, this.level, message);
				} else {
					this.logger.log(FQN, this.level, message, t);
				}
			}
		}

		@Override
		public void log(@NonNull String message, Object... arguments) {
			if( shouldLog() ) {
				Throwable t = this.t;
				if (t == null) {
					this.logger.logf(FQN,this.level, message, arguments);
				} else {
					this.logger.logf(FQN,this.level, message, t, arguments);
				}				
			}
		}

		@Override
		public void log(@NonNull Supplier<@NonNull CharSequence> messageSupplier) {
			log(messageSupplier.get());
		}

		@Override
		public <T> void log(@NonNull Function<T, @NonNull CharSequence> messageSupplier, T type) {
			log(messageSupplier.apply(type));
		}

		@Override
		public FluentLogContext withException(Throwable t) {
			this.t = t;
			return this;
		}
	}
	
	/**
	 * Mutable state of the long context
	 */
	public static class MutableStateImpl implements MutableState {
		private AtomicLong callCount = new AtomicLong();
		private AtomicLong lastLogCount = new AtomicLong();
		private AtomicLong lastLogTime = new AtomicLong();
		
		/**
		 * Create new state
		 */
		public MutableStateImpl() {
		}
		
		public long incrementCallCount() {
			return this.callCount.incrementAndGet();
		}
		
		@Override
		public long currentCallCount() {
			return this.callCount.get();
		}
		
		public void recordLastLog() {
			this.lastLogTime.set(System.nanoTime());
			this.lastLogCount.set(currentCallCount());
		}
		
		@Override
		public long lastLogCount() {
			return this.lastLogCount.get();
		}
		
		@Override
		public long lastLogTime() {
			return this.lastLogTime.get();
		}
	}

	public FluentLoggerImpl(@NonNull Logger logger) {
		this.logger = logger;
	}

	@Override
	public FluentLogContext at(@NonNull Level level) {
		if (this.logger.isEnabled(level)) {
			return new FluentLogContextImpl(level, this.logger); 
		}
		return NO_LOG;
	}
}
