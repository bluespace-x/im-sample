package com.lingyun.lib.comm.result

import java.lang.Exception

/*
* Created by mc_luo on 7/6/21 1:50 PM.
* Copyright (c) 2021 The LingYun Authors. All rights reserved.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
sealed class StepResult<T : Any>(open val step: Int, open val totalStepCount: Int, open val result: T?)

data class StepResultSuccess<T : Any>(override val step: Int, override val totalStepCount: Int, override val result: T?) : StepResult<T>(step, totalStepCount, result)

data class StepResultFail<T : Any>(override val step: Int, override val totalStepCount: Int, val exception: Exception) : StepResult<T>(step, totalStepCount, null)
