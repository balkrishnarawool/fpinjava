package fpinjava.chapter3;

public interface Result<T> {
    // If it isn't good idea to apply effects (except for a very specific place - because we can't completely avoid  them),
    // why are we applying them here?
    void bind(Effect<T> success, Effect<String> failure);

    static <T> Success<T> success(T value) { return new Result.Success<>(value); }
    static <T> Failure<T> failure(String error) { return new Result.Failure<>(error); }

    class Success<T> implements Result<T> {
        private T value;

        public Success(T value) {
            this.value = value;
        }

        // If it isn't good idea to apply effects (except for a very specific place - because we can't completely avoid  them),
        // why are we applying them here?
        @Override
        public void bind(Effect<T> success, Effect<String> failure) {
            success.apply(value);
        }
    }

    class Failure<T> implements Result<T> {
        private String message;

        public Failure(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        // If it isn't good idea to apply effects (except for a very specific place - because we can't completely avoid  them),
        // why are we applying them here?
        @Override
        public void bind(Effect<T> success, Effect<String> failure) {
            failure.apply(message);
        }
    }
}
