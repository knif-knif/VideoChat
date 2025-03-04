<template>
    <ContentField>
        <div class="row">
            <div class="col-lg-2">
                <div class="friend">
                </div>
            </div>
            <div class="col">
                <slot></slot>
            </div>
        </div>
    </ContentField>
</template>

<script setup>
import ContentField from './ContentField'

</script>

<style scoped>
.friend{
    height: 710px;
    background-color: white;
    box-shadow: inset 0 0 10px rgba(0,0,0,0.1);
}
</style>