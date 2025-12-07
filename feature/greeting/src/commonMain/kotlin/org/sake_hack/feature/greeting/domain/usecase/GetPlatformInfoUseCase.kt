package org.sake_hack.feature.greeting.domain.usecase

import org.sake_hack.feature.greeting.domain.model.getPlatform

/**
 * Use case for retrieving platform information.
 * Follows Clean Architecture pattern - contains business logic.
 */
class GetPlatformInfoUseCase {
    operator fun invoke(): String {
        val platform = getPlatform()
        return platform.name
    }
}
