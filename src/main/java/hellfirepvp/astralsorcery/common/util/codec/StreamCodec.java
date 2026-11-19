/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.codec;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Function5;
import com.mojang.datafixers.util.Function6;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/** A typed encoder and decoder for a Forge packet buffer. */
public interface StreamCodec<B, V> {

    V decode(B buffer);

    void encode(B buffer, V value);

    static <B, V> StreamCodec<B, V> of(BiConsumer<B, V> encoder, Function<B, V> decoder) {
        return new StreamCodec<>() {
            @Override
            public V decode(B buffer) {
                return decoder.apply(buffer);
            }

            @Override
            public void encode(B buffer, V value) {
                encoder.accept(buffer, value);
            }
        };
    }

    static <B, V> StreamCodec<B, V> unit(V constant) {
        return of((buffer, value) -> {
            if (!Objects.equals(constant, value)) {
                throw new EncoderException("Expected " + constant + " but received " + value);
            }
        }, buffer -> constant);
    }

    default <R> StreamCodec<B, R> map(Function<? super V, ? extends R> read,
                                      Function<? super R, ? extends V> write) {
        return of((buffer, value) -> this.encode(buffer, write.apply(value)),
                buffer -> read.apply(this.decode(buffer)));
    }

    default <R> StreamCodec<B, R> apply(CodecOperation<B, V, R> operation) {
        return operation.apply(this);
    }

    default <T> StreamCodec<B, T> dispatch(Function<? super T, ? extends V> keyFor,
                                           Function<? super V, ? extends StreamCodec<? super B, ? extends T>> codecFor) {
        return of((buffer, value) -> {
            V key = keyFor.apply(value);
            this.encode(buffer, key);
            StreamCodec<? super B, ? extends T> codec = codecFor.apply(key);
            if (codec == null) {
                throw new EncoderException("Unknown codec key " + key);
            }
            encodeVariant(codec, buffer, value);
        }, buffer -> {
            V key = this.decode(buffer);
            StreamCodec<? super B, ? extends T> codec = codecFor.apply(key);
            if (codec == null) {
                throw new DecoderException("Unknown codec key " + key);
            }
            return codec.decode(buffer);
        });
    }

    @SuppressWarnings("unchecked")
    private static <B, T> void encodeVariant(StreamCodec<? super B, ? extends T> codec, B buffer, T value) {
        ((StreamCodec<? super B, T>) codec).encode(buffer, value);
    }

    @FunctionalInterface
    interface CodecOperation<B, V, R> {
        StreamCodec<B, R> apply(StreamCodec<B, V> codec);
    }

    static <B, R, T1> StreamCodec<B, R> composite(
            StreamCodec<? super B, T1> codec1, Function<R, T1> field1,
            Function<T1, R> factory) {
        return of((buffer, value) -> {
            codec1.encode(buffer, field1.apply(value));
        }, buffer -> factory.apply(codec1.decode(buffer)));
    }

    static <B, R, T1, T2> StreamCodec<B, R> composite(
            StreamCodec<? super B, T1> codec1, Function<R, T1> field1,
            StreamCodec<? super B, T2> codec2, Function<R, T2> field2,
            BiFunction<T1, T2, R> factory) {
        return of((buffer, value) -> {
            codec1.encode(buffer, field1.apply(value));
            codec2.encode(buffer, field2.apply(value));
        }, buffer -> factory.apply(codec1.decode(buffer), codec2.decode(buffer)));
    }

    static <B, R, T1, T2, T3> StreamCodec<B, R> composite(
            StreamCodec<? super B, T1> codec1, Function<R, T1> field1,
            StreamCodec<? super B, T2> codec2, Function<R, T2> field2,
            StreamCodec<? super B, T3> codec3, Function<R, T3> field3,
            Function3<T1, T2, T3, R> factory) {
        return of((buffer, value) -> {
            codec1.encode(buffer, field1.apply(value));
            codec2.encode(buffer, field2.apply(value));
            codec3.encode(buffer, field3.apply(value));
        }, buffer -> factory.apply(codec1.decode(buffer), codec2.decode(buffer), codec3.decode(buffer)));
    }

    static <B, R, T1, T2, T3, T4> StreamCodec<B, R> composite(
            StreamCodec<? super B, T1> codec1, Function<R, T1> field1,
            StreamCodec<? super B, T2> codec2, Function<R, T2> field2,
            StreamCodec<? super B, T3> codec3, Function<R, T3> field3,
            StreamCodec<? super B, T4> codec4, Function<R, T4> field4,
            Function4<T1, T2, T3, T4, R> factory) {
        return of((buffer, value) -> {
            codec1.encode(buffer, field1.apply(value));
            codec2.encode(buffer, field2.apply(value));
            codec3.encode(buffer, field3.apply(value));
            codec4.encode(buffer, field4.apply(value));
        }, buffer -> factory.apply(codec1.decode(buffer), codec2.decode(buffer), codec3.decode(buffer), codec4.decode(buffer)));
    }

    static <B, R, T1, T2, T3, T4, T5> StreamCodec<B, R> composite(
            StreamCodec<? super B, T1> codec1, Function<R, T1> field1,
            StreamCodec<? super B, T2> codec2, Function<R, T2> field2,
            StreamCodec<? super B, T3> codec3, Function<R, T3> field3,
            StreamCodec<? super B, T4> codec4, Function<R, T4> field4,
            StreamCodec<? super B, T5> codec5, Function<R, T5> field5,
            Function5<T1, T2, T3, T4, T5, R> factory) {
        return of((buffer, value) -> {
            codec1.encode(buffer, field1.apply(value));
            codec2.encode(buffer, field2.apply(value));
            codec3.encode(buffer, field3.apply(value));
            codec4.encode(buffer, field4.apply(value));
            codec5.encode(buffer, field5.apply(value));
        }, buffer -> factory.apply(codec1.decode(buffer), codec2.decode(buffer), codec3.decode(buffer), codec4.decode(buffer), codec5.decode(buffer)));
    }

    static <B, R, T1, T2, T3, T4, T5, T6> StreamCodec<B, R> composite(
            StreamCodec<? super B, T1> codec1, Function<R, T1> field1,
            StreamCodec<? super B, T2> codec2, Function<R, T2> field2,
            StreamCodec<? super B, T3> codec3, Function<R, T3> field3,
            StreamCodec<? super B, T4> codec4, Function<R, T4> field4,
            StreamCodec<? super B, T5> codec5, Function<R, T5> field5,
            StreamCodec<? super B, T6> codec6, Function<R, T6> field6,
            Function6<T1, T2, T3, T4, T5, T6, R> factory) {
        return of((buffer, value) -> {
            codec1.encode(buffer, field1.apply(value));
            codec2.encode(buffer, field2.apply(value));
            codec3.encode(buffer, field3.apply(value));
            codec4.encode(buffer, field4.apply(value));
            codec5.encode(buffer, field5.apply(value));
            codec6.encode(buffer, field6.apply(value));
        }, buffer -> factory.apply(codec1.decode(buffer), codec2.decode(buffer), codec3.decode(buffer), codec4.decode(buffer), codec5.decode(buffer), codec6.decode(buffer)));
    }
}
