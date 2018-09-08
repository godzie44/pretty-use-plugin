<?php

use Symfony\Component\HttpKernel\Kernel;

use Shared\CallbackBundle\Dto\Service\Callback\Result\UpdateCallbackDto as UpdateCallbackResult;
use Shared\WidgetBundle\Entity\Widget;

use JMS\DiExtraBundle\Annotation as DI;
use Telephony\ApiBundle\Dto\Controller\Callback\Request\CreateCallbackDto;
use Zzzz;

class PrettyClass {
    public function bar()
    {
        echo 'bar';
    }
}