package com.rsicarelli.homehunt_kmm.domain.usecase

import com.rsicarelli.homehunt_kmm.core.model.UseCase
import com.rsicarelli.homehunt_kmm.domain.repository.UserRepository
import com.rsicarelli.homehunt_kmm.domain.usecase.SignInUseCase.Outcome
import com.rsicarelli.homehunt_kmm.domain.usecase.SignInUseCase.Request
import com.rsicarelli.homehunt_kmm.type.UserInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SignInUseCase(
    private val userRepository: UserRepository
) : UseCase<Request, Outcome> {
    override operator fun invoke(request: Request) = flow {
        val (userName, password) = request
        kotlin.runCatching {
            userRepository.signIn(
                UserInput(
                    password = password,
                    userName = userName
                )
            )
        }
            .onSuccess { emit(Outcome.Success) }
            .onFailure { emit(Outcome.Error) }

    }.flowOn(Dispatchers.Default) //TODO: proper handle threading with KMM

    data class Request(
        val userName: String,
        val password: String
    )

    sealed class Outcome {
        object Success : Outcome()
        object Error : Outcome()
    }
}