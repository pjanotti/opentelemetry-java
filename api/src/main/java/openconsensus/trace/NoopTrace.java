/*
 * Copyright 2019, OpenConsensus Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package openconsensus.trace;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.Nullable;
import openconsensus.context.NoopScope;
import openconsensus.context.Scope;
import openconsensus.internal.Utils;
import openconsensus.trace.data.SpanData;
import openconsensus.trace.propagation.BaseBinaryFormat;
import openconsensus.trace.propagation.BasePropagationComponent;
import openconsensus.trace.propagation.BaseTextFormat;
import openconsensus.trace.propagation.BinaryFormat;
import openconsensus.trace.propagation.PropagationComponent;
import openconsensus.trace.propagation.TextFormat;

/** No-op implementations of trace classes. */
// TODO(Issue #65): decide whether this class should be public or not.
final class NoopTrace {

  private NoopTrace() {}

  /**
   * Returns an instance that contains no-op implementations for all the instances.
   *
   * @return an instance that contains no-op implementations for all the instances.
   */
  static TraceComponent newNoopTraceComponent() {
    return new NoopTraceComponent();
  }

  private static final class NoopTraceComponent extends BaseTraceComponent {
    @Override
    public Tracer getTracer() {
      return getNoopTracer();
    }

    @Override
    public PropagationComponent getPropagationComponent() {
      return getNoopPropagationComponent();
    }
  }

  /**
   * Returns the no-op implementation of the {@code Tracer}.
   *
   * @return the no-op implementation of the {@code Tracer}.
   */
  private static Tracer getNoopTracer() {
    return new NoopTracer();
  }

  // No-Op implementation of the Tracer.
  private static final class NoopTracer extends BaseTracer {

    @Override
    public Span getCurrentSpan() {
      return BlankSpan.INSTANCE;
    }

    @Override
    public Scope withSpan(Span span) {
      return NoopScope.getInstance();
    }

    @Override
    public Runnable withSpan(Span span, Runnable runnable) {
      return runnable;
    }

    @Override
    public <C> Callable<C> withSpan(Span span, Callable<C> callable) {
      return callable;
    }

    @Override
    public SpanBuilder spanBuilder(String spanName) {
      return spanBuilderWithExplicitParent(spanName, getCurrentSpan());
    }

    @Override
    public SpanBuilder spanBuilderWithExplicitParent(String spanName, @Nullable Span parent) {
      return NoopSpanBuilder.createWithParent(spanName, parent);
    }

    @Override
    public SpanBuilder spanBuilderWithRemoteParent(
        String spanName, @Nullable SpanContext remoteParentSpanContext) {
      return NoopSpanBuilder.createWithRemoteParent(spanName, remoteParentSpanContext);
    }

    @Override
    public void recordSpanData(SpanData span) {
      // Do nothing
    }

    private NoopTracer() {}
  }

  // Noop implementation of SpanBuilder.
  private static final class NoopSpanBuilder extends BaseSpanBuilder {
    static NoopSpanBuilder createWithParent(String spanName, @Nullable Span parent) {
      return new NoopSpanBuilder(spanName);
    }

    static NoopSpanBuilder createWithRemoteParent(
        String spanName, @Nullable SpanContext remoteParentSpanContext) {
      return new NoopSpanBuilder(spanName);
    }

    @Override
    public Span startSpan() {
      return BlankSpan.INSTANCE;
    }

    @Override
    public void startSpanAndRun(Runnable runnable) {
      runnable.run();
    }

    @Override
    public <V> V startSpanAndCall(Callable<V> callable) throws Exception {
      return callable.call();
    }

    @Override
    public SpanBuilder setSampler(@Nullable Sampler sampler) {
      return this;
    }

    @Override
    public SpanBuilder setParentLinks(List<Span> parentLinks) {
      return this;
    }

    @Override
    public SpanBuilder setRecordEvents(boolean recordEvents) {
      return this;
    }

    @Override
    public SpanBuilder setSpanKind(Span.Kind spanKind) {
      return this;
    }

    private NoopSpanBuilder(String name) {
      Utils.checkNotNull(name, "name");
    }
  }

  /**
   * Returns an instance that contains no-op implementations for all the instances.
   *
   * @return an instance that contains no-op implementations for all the instances.
   */
  private static PropagationComponent getNoopPropagationComponent() {
    return new NoopPropagationComponent();
  }

  private static final class NoopPropagationComponent extends BasePropagationComponent {

    @Override
    public BinaryFormat getBinaryFormat() {
      return getNoopBinaryFormat();
    }

    @Override
    public TextFormat getB3Format() {
      return getNoopTextFormat();
    }

    @Override
    public TextFormat getTraceContextFormat() {
      return getNoopTextFormat();
    }
  }

  /**
   * Returns the no-op implementation of the {@code BinaryFormat}.
   *
   * @return the no-op implementation of the {@code BinaryFormat}.
   */
  private static BinaryFormat getNoopBinaryFormat() {
    return new NoopBinaryFormat();
  }

  private static final class NoopBinaryFormat extends BaseBinaryFormat {

    @Override
    public byte[] toByteArray(SpanContext spanContext) {
      Utils.checkNotNull(spanContext, "spanContext");
      return new byte[0];
    }

    @Override
    public SpanContext fromByteArray(byte[] bytes) {
      Utils.checkNotNull(bytes, "bytes");
      return SpanContext.INVALID;
    }

    private NoopBinaryFormat() {}
  }

  /**
   * Returns the no-op implementation of the {@code TextFormat}.
   *
   * @return the no-op implementation of the {@code TextFormat}.
   */
  private static TextFormat getNoopTextFormat() {
    return new NoopTextFormat();
  }

  private static final class NoopTextFormat extends BaseTextFormat {

    private NoopTextFormat() {}

    @Override
    public List<String> fields() {
      return Collections.emptyList();
    }

    @Override
    public <C> void inject(SpanContext spanContext, C carrier, Setter<C> setter) {
      Utils.checkNotNull(spanContext, "spanContext");
      Utils.checkNotNull(carrier, "carrier");
      Utils.checkNotNull(setter, "setter");
    }

    @Override
    public <C> SpanContext extract(C carrier, Getter<C> getter) {
      Utils.checkNotNull(carrier, "carrier");
      Utils.checkNotNull(getter, "getter");
      return SpanContext.INVALID;
    }
  }
}
