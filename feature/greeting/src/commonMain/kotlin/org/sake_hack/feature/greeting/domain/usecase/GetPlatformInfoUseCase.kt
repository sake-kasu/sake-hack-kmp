package org.sake_hack.feature.greeting.domain.usecase

import org.sake_hack.feature.greeting.domain.model.getPlatform
import kotlin.experimental.ExperimentalObjCName
import kotlin.native.ObjCName

/**
 * Use case for retrieving platform information.
 * Follows Clean Architecture pattern - contains business logic.
 */
@OptIn(ExperimentalObjCName::class)
@ObjCName("GetPlatformInfoUseCase")
class GetPlatformInfoUseCase {
    @ObjCName("invoke")
    operator fun invoke(): String {
        val platform = getPlatform()
        return platform.name
    }
}
